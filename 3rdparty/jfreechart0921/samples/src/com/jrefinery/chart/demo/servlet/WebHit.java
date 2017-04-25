package com.jrefinery.chart.demo.servlet;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Richard Atkinson
 * @version 1.0
 */

import java.util.Date;

public class WebHit {
	protected Date hitDate = null;
	protected String section = null;
	protected long hitCount = 0;

    public WebHit(Date dHitDate, String sSection, long lHitCount) {
		this.hitDate = dHitDate;
		this.section = sSection;
		this.hitCount = lHitCount;
    }

	public Date getHitDate() {
		return this.hitDate;
	}
	public String getSection() {
		return this.section;
	}
	public long getHitCount() {
		return this.hitCount;
	}

	public void setHitDate(Date dHitDate) {
		this.hitDate = dHitDate;
	}
	public void setSection(String sSection) {
		this.section = sSection;
	}
	public void setHitCount(long lHitCount) {
		this.hitCount = lHitCount;
	}

}