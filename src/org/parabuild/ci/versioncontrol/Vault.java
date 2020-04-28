/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.versioncontrol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Vault history
 */
public class Vault {


  private History history = null;
  private Error error = null;
  private Result result = null;


  public History getHistory() {
    return history;
  }


  public void setHistory(final History history) {
    this.history = history;
  }


  public Error getError() {
    return error;
  }


  public void setError(final Error error) {
    this.error = error;
  }


  public Result getResult() {
    return result;
  }


  public void setResult(final Result result) {
    this.result = result;
  }


  public String toString() {
    return "vault{" +
      "history=" + history +
      ", error=" + error +
      ", result=" + result +
      '}';
  }


  /**
   * history element.
   */
  public static class History {

    private List items = new ArrayList(11);


    public void addItem(final Item item) {
      items.add(item);
    }


    public List getItems() {
      return items;
    }


    public void setItems(final List items) {
      this.items = items;
    }


    public String toString() {
      return "History{" +
        "items=" + items +
        '}';
    }
  }

  /**
   * error element.
   */
  public static class Error {

    private String message = null;


    public String getMessage() {
      return message;
    }


    public void setMessage(final String message) {
      this.message = message;
    }


    public String toString() {
      return "Error{" +
        "message=" + message +
        '}';
    }
  }

  /**
   * result element.
   */
  public static class Result {

    private boolean success = false;


    public boolean isSuccess() {
      return success;
    }


    public void setSuccess(final String success) {
      this.success = "yes".equals(success);
    }


    public String toString() {
      return "Exit{" +
        "success=" + success +
        '}';
    }
  }

  public static final class Item {

    /**
     * Natural txid comparator.
     */
    public static final Comparator TX_ID_COMPARATOR = new Comparator() {
      public int compare(final Object o1, final Object o2) {
        if (!(o1 instanceof Item)) return 0;
        final Item brp1 = (Item)o1;
        final Item brp2 = (Item)o2;
        if (brp1.getTxid() > brp2.getTxid()) return 1;
        if (brp1.getTxid() == brp2.getTxid()) return 0;
        if (brp1.getTxid() < brp2.getTxid()) return -1;
        return brp1.getTxid();
      }
    };

    /**
     * Reverse txid comparator.
     */
    public static final Comparator REVERSE_TX_ID_COMPARATOR = new Comparator() {
      public int compare(final Object o1, final Object o2) {
        return REVERSE_TX_ID_COMPARATOR.compare(o2, o1);
      }
    };

    //  <item txid="2" date="11/30/2005 1:05:10 AM" name="$" type="70" version="1" user="admin" comment="creating repository" actionString="Created" />

    private int txid = -1;
    private int type = -1;
    private int version = -1;
    private String date = null;
    private String name = null;
    private String user = null;
    private String comment = null;
    private String actionString = null;

    public int getTxid() {
      return txid;
    }


    public void setTxid(final int txid) {
      this.txid = txid;
    }


    public int getType() {
      return type;
    }


    public void setType(final int type) {
      this.type = type;
    }


    public int getVersion() {
      return version;
    }


    public void setVersion(final int version) {
      this.version = version;
    }


    public String getDate() {
      return date;
    }


    public void setDate(final String date) {
      this.date = date;
    }


    public String getName() {
      return name;
    }


    public void setName(final String name) {
      this.name = name;
    }


    public String getUser() {
      return user;
    }


    public void setUser(final String user) {
      this.user = user;
    }


    public String getComment() {
      return comment;
    }


    public void setComment(final String comment) {
      this.comment = comment;
    }


    public String getActionString() {
      return actionString;
    }


    public void setActionString(final String actionString) {
      this.actionString = actionString;
    }


    public String toString() {
      return "Item{" +
        "txid=" + txid +
        ", type=" + type +
        ", version=" + version +
        ", date=" + date +
        ", name='" + name + '\'' +
        ", user='" + user + '\'' +
        ", comment='" + comment + '\'' +
        ", actionString='" + actionString + '\'' +
        '}';
    }
  }
}
