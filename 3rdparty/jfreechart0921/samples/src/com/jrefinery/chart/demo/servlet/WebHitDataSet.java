package com.jrefinery.chart.demo.servlet;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Richard Atkinson
 * @version 1.0
 */
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;

public class WebHitDataSet {
	protected ArrayList data = new ArrayList();

    public WebHitDataSet() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		data.add(new WebHit(sdf.parse("01-Aug-2002"), "Catalog", 201923));
		data.add(new WebHit(sdf.parse("02-Aug-2002"), "Catalog", 213125));
		data.add(new WebHit(sdf.parse("05-Aug-2002"), "Catalog", 222148));
		data.add(new WebHit(sdf.parse("06-Aug-2002"), "Catalog", 217434));
		data.add(new WebHit(sdf.parse("07-Aug-2002"), "Catalog", 233256));
		data.add(new WebHit(sdf.parse("08-Aug-2002"), "Catalog", 257654));
		data.add(new WebHit(sdf.parse("09-Aug-2002"), "Catalog", 295356));
		data.add(new WebHit(sdf.parse("12-Aug-2002"), "Catalog", 222567));
		data.add(new WebHit(sdf.parse("13-Aug-2002"), "Catalog", 246343));
		data.add(new WebHit(sdf.parse("14-Aug-2002"), "Catalog", 284558));
		data.add(new WebHit(sdf.parse("15-Aug-2002"), "Catalog", 326524));
		data.add(new WebHit(sdf.parse("16-Aug-2002"), "Catalog", 335234));
		data.add(new WebHit(sdf.parse("19-Aug-2002"), "Catalog", 373442));
		data.add(new WebHit(sdf.parse("20-Aug-2002"), "Catalog", 353675));
		data.add(new WebHit(sdf.parse("21-Aug-2002"), "Catalog", 426434));
		data.add(new WebHit(sdf.parse("22-Aug-2002"), "Catalog", 396558));
		data.add(new WebHit(sdf.parse("23-Aug-2002"), "Catalog", 442655));
		data.add(new WebHit(sdf.parse("26-Aug-2002"), "Catalog", 432562));
		data.add(new WebHit(sdf.parse("27-Aug-2002"), "Catalog", 423226));
		data.add(new WebHit(sdf.parse("28-Aug-2002"), "Catalog", 452626));

		data.add(new WebHit(sdf.parse("01-Aug-2002"), "Checkout", 32355));
		data.add(new WebHit(sdf.parse("02-Aug-2002"), "Checkout", 28543));
		data.add(new WebHit(sdf.parse("05-Aug-2002"), "Checkout", 29665));
		data.add(new WebHit(sdf.parse("06-Aug-2002"), "Checkout", 34567));
		data.add(new WebHit(sdf.parse("07-Aug-2002"), "Checkout", 32453));
		data.add(new WebHit(sdf.parse("08-Aug-2002"), "Checkout", 29455));
		data.add(new WebHit(sdf.parse("09-Aug-2002"), "Checkout", 28558));
		data.add(new WebHit(sdf.parse("12-Aug-2002"), "Checkout", 31084));
		data.add(new WebHit(sdf.parse("13-Aug-2002"), "Checkout", 32568));
		data.add(new WebHit(sdf.parse("14-Aug-2002"), "Checkout", 33563));
		data.add(new WebHit(sdf.parse("15-Aug-2002"), "Checkout", 35675));
		data.add(new WebHit(sdf.parse("16-Aug-2002"), "Checkout", 37568));
		data.add(new WebHit(sdf.parse("19-Aug-2002"), "Checkout", 38764));
		data.add(new WebHit(sdf.parse("20-Aug-2002"), "Checkout", 35787));
		data.add(new WebHit(sdf.parse("21-Aug-2002"), "Checkout", 37865));
		data.add(new WebHit(sdf.parse("22-Aug-2002"), "Checkout", 39563));
		data.add(new WebHit(sdf.parse("23-Aug-2002"), "Checkout", 40291));
		data.add(new WebHit(sdf.parse("26-Aug-2002"), "Checkout", 39576));
		data.add(new WebHit(sdf.parse("27-Aug-2002"), "Checkout", 43623));
		data.add(new WebHit(sdf.parse("28-Aug-2002"), "Checkout", 41436));

		data.add(new WebHit(sdf.parse("01-Aug-2002"), "Tracking", 45344));
		data.add(new WebHit(sdf.parse("02-Aug-2002"), "Tracking", 43222));
		data.add(new WebHit(sdf.parse("05-Aug-2002"), "Tracking", 44567));
		data.add(new WebHit(sdf.parse("06-Aug-2002"), "Tracking", 46435));
		data.add(new WebHit(sdf.parse("07-Aug-2002"), "Tracking", 42538));
		data.add(new WebHit(sdf.parse("08-Aug-2002"), "Tracking", 39553));
		data.add(new WebHit(sdf.parse("09-Aug-2002"), "Tracking", 44565));
		data.add(new WebHit(sdf.parse("12-Aug-2002"), "Tracking", 46548));
		data.add(new WebHit(sdf.parse("13-Aug-2002"), "Tracking", 55433));
		data.add(new WebHit(sdf.parse("14-Aug-2002"), "Tracking", 58548));
		data.add(new WebHit(sdf.parse("15-Aug-2002"), "Tracking", 45453));
		data.add(new WebHit(sdf.parse("16-Aug-2002"), "Tracking", 34565));
		data.add(new WebHit(sdf.parse("19-Aug-2002"), "Tracking", 56678));
		data.add(new WebHit(sdf.parse("20-Aug-2002"), "Tracking", 54569));
		data.add(new WebHit(sdf.parse("21-Aug-2002"), "Tracking", 56843));
		data.add(new WebHit(sdf.parse("22-Aug-2002"), "Tracking", 43772));
		data.add(new WebHit(sdf.parse("23-Aug-2002"), "Tracking", 32655));
		data.add(new WebHit(sdf.parse("26-Aug-2002"), "Tracking", 39564));
		data.add(new WebHit(sdf.parse("27-Aug-2002"), "Tracking", 37643));
		data.add(new WebHit(sdf.parse("28-Aug-2002"), "Tracking", 34763));

		data.add(new WebHit(sdf.parse("01-Aug-2002"), "Service", 55437));
		data.add(new WebHit(sdf.parse("02-Aug-2002"), "Service", 55745));
		data.add(new WebHit(sdf.parse("05-Aug-2002"), "Service", 52523));
		data.add(new WebHit(sdf.parse("06-Aug-2002"), "Service", 48563));
		data.add(new WebHit(sdf.parse("07-Aug-2002"), "Service", 34675));
		data.add(new WebHit(sdf.parse("08-Aug-2002"), "Service", 29455));
		data.add(new WebHit(sdf.parse("09-Aug-2002"), "Service", 43678));
		data.add(new WebHit(sdf.parse("12-Aug-2002"), "Service", 64377));
		data.add(new WebHit(sdf.parse("13-Aug-2002"), "Service", 43677));
		data.add(new WebHit(sdf.parse("14-Aug-2002"), "Service", 37574));
		data.add(new WebHit(sdf.parse("15-Aug-2002"), "Service", 32645));
		data.add(new WebHit(sdf.parse("16-Aug-2002"), "Service", 35345));
		data.add(new WebHit(sdf.parse("19-Aug-2002"), "Service", 26785));
		data.add(new WebHit(sdf.parse("20-Aug-2002"), "Service", 24754));
		data.add(new WebHit(sdf.parse("21-Aug-2002"), "Service", 22467));
		data.add(new WebHit(sdf.parse("22-Aug-2002"), "Service", 18545));
		data.add(new WebHit(sdf.parse("23-Aug-2002"), "Service", 20567));
		data.add(new WebHit(sdf.parse("26-Aug-2002"), "Service", 19325));
		data.add(new WebHit(sdf.parse("27-Aug-2002"), "Service", 17343));
		data.add(new WebHit(sdf.parse("28-Aug-2002"), "Service", 18533));
    }

	public ArrayList getDataByHitDate(String filterSection) {
		ArrayList results = new ArrayList();
		HashMap dateMap = new HashMap();
		Iterator iter = this.data.listIterator();
		int currentPosition = 0;
		while (iter.hasNext()) {
			WebHit webHit = (WebHit)iter.next();
			if (filterSection == null ? true : filterSection.equals(webHit.getSection())) {
				Integer position = (Integer)dateMap.get(webHit.getHitDate());
				if (position == null) {
					results.add(webHit);
					dateMap.put(webHit.getHitDate(), new Integer(currentPosition));
					currentPosition++;
				} else {
					WebHit previousWebHit = (WebHit)results.get(position.intValue());
					previousWebHit.setHitCount(previousWebHit.getHitCount() + webHit.getHitCount());
				}
			}

		}
		return results;
	}

	public ArrayList getDataBySection(Date filterHitDate) {
		ArrayList results = new ArrayList();
		HashMap sectionMap = new HashMap();
		Iterator iter = this.data.listIterator();
		int currentPosition = 0;
		while (iter.hasNext()) {
			WebHit webHit = (WebHit)iter.next();
			if (filterHitDate == null ? true : filterHitDate.equals(webHit.getHitDate())) {
				Integer position = (Integer)sectionMap.get(webHit.getSection());
				if (position == null) {
					results.add(webHit);
					sectionMap.put(webHit.getSection(), new Integer(currentPosition));
					currentPosition++;
				} else {
					WebHit previousWebHit = (WebHit)results.get(position.intValue());
					previousWebHit.setHitCount(previousWebHit.getHitCount() + webHit.getHitCount());
				}
			}
		}
		return results;
	}

	public static void main(java.lang.String[] args) {
		try {
			WebHitDataSet whDataSet = new WebHitDataSet();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			ArrayList results = whDataSet.getDataBySection(sdf.parse("01-Aug-2002"));
			Iterator iter = results.listIterator();
			while (iter.hasNext()) {
				WebHit wh = (WebHit)iter.next();
				System.out.println(wh.getSection() + " - " + wh.getHitCount());
			}
			System.out.println("Finished.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList getDateList() {
		ArrayList dateList = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			dateList.add(sdf.parse("28-Aug-2002"));
			dateList.add(sdf.parse("27-Aug-2002"));
			dateList.add(sdf.parse("26-Aug-2002"));
			dateList.add(sdf.parse("23-Aug-2002"));
			dateList.add(sdf.parse("22-Aug-2002"));
			dateList.add(sdf.parse("21-Aug-2002"));
			dateList.add(sdf.parse("20-Aug-2002"));
			dateList.add(sdf.parse("19-Aug-2002"));
			dateList.add(sdf.parse("16-Aug-2002"));
			dateList.add(sdf.parse("15-Aug-2002"));
			dateList.add(sdf.parse("14-Aug-2002"));
			dateList.add(sdf.parse("13-Aug-2002"));
			dateList.add(sdf.parse("12-Aug-2002"));
			dateList.add(sdf.parse("09-Aug-2002"));
			dateList.add(sdf.parse("08-Aug-2002"));
			dateList.add(sdf.parse("07-Aug-2002"));
			dateList.add(sdf.parse("06-Aug-2002"));
			dateList.add(sdf.parse("05-Aug-2002"));
			dateList.add(sdf.parse("02-Aug-2002"));
			dateList.add(sdf.parse("01-Aug-2002"));
		} catch (ParseException e) {
			// ignore
		}
		return dateList;
	}

	public static ArrayList getSectionList() {
		ArrayList sectionList = new ArrayList();
		sectionList.add("Catalog");
		sectionList.add("Checkout");
		sectionList.add("Service");
		sectionList.add("Tracking");
		return sectionList;
	}

}