/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;

/**
 * User-bound façade of the Apex metrics framework.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated Use {@link MetricsUtil}
 */
@Deprecated
public final class ApexMetrics {

    private static final ApexMetricsFacade FACADE = new ApexMetricsFacade();


    private ApexMetrics() { // Cannot be instantiated

    }


    /**
     * Returns the underlying facade.
     *
     * @return The facade
     */
    @Deprecated
    public static ApexMetricsFacade getFacade() {
        return FACADE;
    }


    /**
     * Computes the standard value of the metric identified by its code on a class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTUserClassOrInterface<?>> key, ASTUserClass node) {
        return get(key, node, MetricOptions.emptyOptions());
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting metric options with the {@code
     * options} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTUserClassOrInterface<?>> key, ASTUserClass node, MetricOptions options) {
        return MetricsUtil.computeMetricOrNaN(key, node, options);
    }


    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethod> key, ASTMethod node) {
        return get(key, node, MetricOptions.emptyOptions());
    }


    /**
     * Computes a metric identified by its key on a operation AST node, possibly selecting metric options with the
     * {@code options} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethod> key, ASTMethod node, MetricOptions options) {
        return MetricsUtil.computeMetricOrNaN(key, node, options);
    }


    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     *     {@code null}
     */
    public static double get(MetricKey<ASTMethod> key, ASTUserClassOrInterface<?> node, ResultOption resultOption) {
        return MetricsUtil.computeAggregate(key, findOps(node), resultOption);
    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param options      The options of the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     *     {@code null}
     */
    public static double get(MetricKey<ASTMethod> key, ASTUserClassOrInterface<?> node, MetricOptions options,
                             ResultOption resultOption) {
        return MetricsUtil.computeAggregate(key, findOps(node), options, resultOption);
    }


    @NonNull
    public static List<ASTMethod> findOps(ASTUserClassOrInterface<?> node) {
        List<ASTMethod> candidates = node.findChildrenOfType(ASTMethod.class);
        List<ASTMethod> result = new ArrayList<>(candidates);
        for (ASTMethod method : candidates) {
            if (method.getImage().matches("(<clinit>|<init>|clone)")) {
                result.remove(method);
            }
        }
        return result;
    }
}
