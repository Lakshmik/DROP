
package org.drip.historical.attribution;

import java.util.Map;
import java.util.Set;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.numerical.common.NumberUtil;
import org.drip.service.common.FormatUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * Copyright (C) 2025 Lakshmi Krishnamurthy
 * Copyright (C) 2024 Lakshmi Krishnamurthy
 * Copyright (C) 2023 Lakshmi Krishnamurthy
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>PositionMarketSnap</i> contains the Metrics Snapshot associated with the relevant Manifest Measures for
 * 	a given Position. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>PositionMarketSnap</i> Constructor</li>
 * 		<li>Retrieve the Date of the Snap</li>
 * 		<li>Retrieve the Position Market Value</li>
 * 		<li>Add an Instance of the Position Manifest Measure Snap from the Specified Inputs</li>
 * 		<li>Retrieve the Snapshot associated with the specified Manifest Measure</li>
 * 		<li>Retrieve the Set of Manifest Measures</li>
 * 		<li>Set the Custom Date Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom Date Entry corresponding to the Specified Key</li>
 * 		<li>Set the Custom C<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom C<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Set the Custom R<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom R<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Set the Market Measure Name</li>
 * 		<li>Retrieve the Market Measure Name</li>
 * 		<li>Set the Market Measure Value</li>
 * 		<li>Retrieve the Market Measure Value</li>
 * 		<li>Set the Cumulative Coupon Amount</li>
 * 		<li>Retrieve the Cumulative Coupon Amount</li>
 * 		<li>Retrieve the Row of Header Fields</li>
 * 		<li>Retrieve the Row of Content Fields</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/attribution/README.md">Position Market Change Components Attribution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class PositionMarketSnap
{
	private JulianDate _snapDate = null;
	private double _marketValue = Double.NaN;

	private Map<String, String> _customC1Map = new CaseInsensitiveHashMap<String>();

	private Map<String, Double> _customR1Map = new CaseInsensitiveHashMap<Double>();

	private Map<String, JulianDate> _customDateMap = new CaseInsensitiveHashMap<JulianDate>();

	private Map<String, PositionManifestMeasureSnap> _positionManifestMeasureSnapMap =
		new CaseInsensitiveHashMap<PositionManifestMeasureSnap>();

	/**
	 * <i>PositionMarketSnap</i> Constructor
	 * 
	 * @param snapDate The Snapshot Date
	 * @param marketValue The Snapshot Market Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public PositionMarketSnap (
		final JulianDate snapDate,
		final double marketValue)
		throws Exception
	{
		if (null == (_snapDate = snapDate) || !NumberUtil.IsValid (_marketValue = marketValue)) {
			throw new Exception ("PositionMarketSnap Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Date of the Snap
	 * 
	 * @return Date of the Snap
	 */

	public JulianDate snapDate()
	{
		return _snapDate;
	}

	/**
	 * Retrieve the Position Market Value
	 * 
	 * @return The Position Market Value
	 */

	public double marketValue()
	{
		return _marketValue;
	}

	/**
	 * Add an Instance of the Position Manifest Measure Snap from the Specified Inputs
	 * 
	 * @param manifestMeasure The Manifest Measure
	 * @param manifestMeasureRealization The Manifest Measure Realization
	 * @param manifestMeasureSensitivity The Manifest Measure Sensitivity
	 * @param manifestMeasureRollDown The Manifest Measure Roll Down
	 * 
	 * @return TRUE - The Manifest Measure Snap Metrics successfully added
	 */

	public boolean addManifestMeasureSnap (
		final String manifestMeasure,
		final double manifestMeasureRealization,
		final double manifestMeasureSensitivity,
		final double manifestMeasureRollDown)
	{
		if (null == manifestMeasure || manifestMeasure.isEmpty()) {
			return false;
		}

		try {
			_positionManifestMeasureSnapMap.put (
				manifestMeasure,
				new PositionManifestMeasureSnap (
					manifestMeasureRealization,
					manifestMeasureSensitivity,
					manifestMeasureRollDown
				)
			);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Retrieve the Snapshot associated with the specified Manifest Measure
	 * 
	 * @param manifestMeasure The Manifest Measure
	 * 
	 * @return The Snapshot associated with the specified Manifest Measure
	 */

	public PositionManifestMeasureSnap manifestMeasureSnap (
		final String manifestMeasure)
	{
		return null == manifestMeasure || !_positionManifestMeasureSnapMap.containsKey (manifestMeasure) ?
			null : _positionManifestMeasureSnapMap.get (manifestMeasure);
	}

	/**
	 * Retrieve the Set of Manifest Measures
	 * 
	 * @return The Set of Manifest Measures
	 */

	public Set<String> manifestMeasures()
	{
		return _positionManifestMeasureSnapMap.keySet();
	}

	/**
	 * Set the Custom Date Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param customDate The Custom Date Entry
	 * 
	 * @return TRUE - Custom Date successfully set
	 */

	public boolean setDate (
		final String key,
		final JulianDate customDate)
	{
		if (null == key || key.isEmpty() || null == customDate) {
			return false;
		}

		_customDateMap.put (key, customDate);

		return true;
	}

	/**
	 * Retrieve the Custom Date Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom Date Entry
	 */

	public JulianDate date (
		final String key)
	{
		return null == key || !_customDateMap.containsKey (key) ? null : _customDateMap.get (key);
	}

	/**
	 * Set the Custom C<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param c1 The Custom C<sup>1</sup> Entry
	 * 
	 * @return TRUE - Custom C<sup>1</sup> Entry successfully set
	 */

	public boolean setC1 (
		final String key,
		final String c1)
	{
		if (null == key || key.isEmpty() || null == c1 || c1.isEmpty()) {
			return false;
		}

		_customC1Map.put (key, c1);

		return true;
	}

	/**
	 * Retrieve the Custom C<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom C<sup>1</sup> Entry
	 */

	public String c1 (
		final String key)
	{
		return null == key || !_customC1Map.containsKey (key) ? null : _customC1Map.get (key);
	}

	/**
	 * Set the Custom R<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param r1 The Custom R<sup>1</sup> Entry
	 * @param ignoreNaN TRUE - Ignore NaN Entry
	 * 
	 * @return TRUE - Custom Number successfully set
	 */

	public boolean setR1 (
		final String key,
		final double r1,
		final boolean ignoreNaN)
	{
		if (null == key || key.isEmpty() || (!ignoreNaN && !NumberUtil.IsValid (r1))) {
			return false;
		}

		_customR1Map.put (key, r1);

		return true;
	}

	/**
	 * Set the Custom R<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param r1 The Custom R<sup>1</sup> Entry
	 * 
	 * @return TRUE - Custom Number successfully set
	 */

	public boolean setR1 (
		final String key,
		final double r1)
	{
		return setR1 (key, r1, true);
	}

	/**
	 * Retrieve the Custom R<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom R<sup>1</sup> Entry
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double r1 (
		final String key)
		throws Exception
	{
		if (null == key || !_customR1Map.containsKey (key)) {
			throw new Exception ("PositionMarketSnap::r1 => Invalid Inputs");
		}

		return _customR1Map.get (key);
	}

	/**
	 * Set the Market Measure Name
	 * 
	 * @param marketMeasureName The Market Measure Name
	 * 
	 * @return The Market Measure Name successfully set
	 */

	public boolean setMarketMeasureName (
		final String marketMeasureName)
	{
		return setC1 ("MarketMeasureName", marketMeasureName);
	}

	/**
	 * Retrieve the Market Measure Name
	 * 
	 * @return The Market Measure Name
	 */

	public String marketMeasureName()
	{
		return c1 ("MarketMeasureName");
	}

	/**
	 * Set the Market Measure Value
	 * 
	 * @param marketMeasureValue The Market Measure Value
	 * 
	 * @return The Market Measure Value successfully set
	 */

	public boolean setMarketMeasureValue (
		final double marketMeasureValue)
	{
		return setR1 ("MarketMeasureValue", marketMeasureValue);
	}

	/**
	 * Retrieve the Market Measure Value
	 * 
	 * @return The Market Measure Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double marketMeasureValue()
		throws Exception
	{
		return r1 ("MarketMeasureValue");
	}

	/**
	 * Set the Cumulative Coupon Amount
	 * 
	 * @param cumulativeCouponAmount The Cumulative Coupon Amount
	 * 
	 * @return TRUE - The Cumulative Coupon Amount successfully set
	 */

	public boolean setCumulativeCouponAmount (
		final double cumulativeCouponAmount)
	{
		return setR1 ("CumulativeCouponAmount", cumulativeCouponAmount);
	}

	/**
	 * Retrieve the Cumulative Coupon Amount
	 * 
	 * @return The Cumulative Coupon Amount
	 * 
	 * @throws Exception Thrown if the Cumulative Coupon Amount cannot be obtained
	 */

	public double cumulativeCouponAmount()
		throws Exception
	{
		return r1 ("CumulativeCouponAmount");
	}

	/**
	 * Retrieve the Row of Header Fields
	 * 
	 * @param prefix The Prefix that precedes each Header Field
	 * 
	 * @return The Row of Header Fields
	 */

	public String header (
		final String prefix)
	{
		String header = "";

		for (String r1Key : _customR1Map.keySet()) {
			header = header + prefix + r1Key + ",";
		}

		for (String c1Key : _customC1Map.keySet()) {
			header = header + prefix + c1Key + ",";
		}

		for (String dateKey : _customDateMap.keySet()) {
			header = header + prefix + dateKey + ",";
		}

		return header;
	}

	/**
	 * Retrieve the Row of Content Fields
	 * 
	 * @return The Row of Content Fields
	 */

	public String content()
	{
		String content = "";

		for (String r1Key : _customR1Map.keySet()) {
			content = content + FormatUtil.FormatDouble (_customR1Map.get (r1Key), 1, 8, 1.) + ",";
		}

		for (String c1Key : _customC1Map.keySet()) {
			content = content + _customC1Map.get (c1Key) + ",";
		}

		for (String dateKey : _customDateMap.keySet()) {
			content = content + _customDateMap.get (dateKey).toString() + ",";
		}

		return content;
	}
}
