
package org.drip.historical.attribution;

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
 * <i>PositionChangeComponents</i> contains the Decomposition of the Components of the Interval Change for a
 * 	given Position. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>PositionChangeComponents</i> Constructor</li>
 * 		<li>Return the Position Change Type</li>
 * 		<li>Retrieve the Set of Manifest Measures</li>
 * 		<li>Retrieve the First Position Market Snapshot Instance</li>
 * 		<li>Retrieve the Second Position Market Snapshot Instance</li>
 * 		<li>Retrieve the First Date</li>
 * 		<li>Retrieve the Second Date</li>
 * 		<li>Retrieve the Gross Interval Clean Change</li>
 * 		<li>Retrieve the Gross Interval Change</li>
 * 		<li>Retrieve the Specific Manifest Measure Market Realization Position Change</li>
 * 		<li>Retrieve the Full Manifest Measure Realization Position Change</li>
 * 		<li>Retrieve the Specific Manifest Measure Market Sensitivity Position Change</li>
 * 		<li>Retrieve the Full Manifest Measure Market Sensitivity Position Change</li>
 * 		<li>Retrieve the Specific Manifest Measure Market Roll-down Position Change</li>
 * 		<li>Retrieve the Full Manifest Measure Roll-down Position Change</li>
 * 		<li>Retrieve the Accrual Interval Change</li>
 * 		<li>Retrieve the Explained Interval Change</li>
 * 		<li>Retrieve the Unexplained Interval Change</li>
 * 		<li>Retrieve the Map of Difference Metrics</li>
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

public class PositionChangeComponents
{
	private boolean _changeTypeReturn = false;
	private double _accrualChange = Double.NaN;
	private PositionMarketSnap _t1PositionMarketSnap = null;
	private PositionMarketSnap _t2PositionMarketSnap = null;
	private CaseInsensitiveHashMap<Double> _differenceMetricMap = null;

	/**
	 * <i>PositionChangeComponents</i> Constructor
	 * 
	 * @param changeTypeReturn TRUE - Change Type is Return (Relative)
	 * @param t1PositionMarketSnap The T1 Position Market Snapshot Instance
	 * @param t2PositionMarketSnap The T2 Position Market Snapshot Instance
	 * @param accrualChange The Accrual Change Component of Interval Return/Change
	 * @param differenceMetricMap The Map of Difference Metrics
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public PositionChangeComponents (
		final boolean changeTypeReturn,
		final PositionMarketSnap t1PositionMarketSnap,
		final PositionMarketSnap t2PositionMarketSnap,
		final double accrualChange,
		final CaseInsensitiveHashMap<Double> differenceMetricMap)
		throws Exception
	{
		_changeTypeReturn = changeTypeReturn;
		_differenceMetricMap = differenceMetricMap;

		if (null == (_t1PositionMarketSnap = t1PositionMarketSnap) ||
			null == (_t2PositionMarketSnap = t2PositionMarketSnap) ||
			_t1PositionMarketSnap.snapDate().julian() >= _t2PositionMarketSnap.snapDate().julian() ||
			!NumberUtil.IsValid (_accrualChange = accrualChange))
		{
			throw new Exception ("PositionChangeComponents Constructor => Invalid Inputs!");
		}
	}

	/**
	 * Return the Position Change Type
	 * 
	 * @return TRUE - Change Type is Return (Relative)
	 */

	public boolean changeTypeReturn()
	{
		return _changeTypeReturn;
	}

	/**
	 * Retrieve the Set of Manifest Measures
	 * 
	 * @return The Set of Manifest Measures
	 */

	public Set<String> manifestMeasureSet()
	{
		return _t1PositionMarketSnap.manifestMeasures();
	}

	/**
	 * Retrieve the First Position Market Snapshot Instance
	 * 
	 * @return The First Position Market Snapshot Instance
	 */

	public PositionMarketSnap t1PositionMarketSnap()
	{
		return _t1PositionMarketSnap;
	}

	/**
	 * Retrieve the Second Position Market Snapshot Instance
	 * 
	 * @return The Second Position Market Snapshot Instance
	 */

	public PositionMarketSnap t2PositionMarketSnap()
	{
		return _t2PositionMarketSnap;
	}

	/**
	 * Retrieve the First Date
	 * 
	 * @return The First Date
	 */

	public JulianDate t1()
	{
		return _t1PositionMarketSnap.snapDate();
	}

	/**
	 * Retrieve the Second Date
	 * 
	 * @return The Second Date
	 */

	public JulianDate t2()
	{
		return _t2PositionMarketSnap.snapDate();
	}

	/**
	 * Retrieve the Gross Interval Clean Change
	 * 
	 * @return The Gross Interval Clean Change
	 */

	public double grossCleanChange()
	{
		return _t2PositionMarketSnap.marketValue() - _t1PositionMarketSnap.marketValue();
	}

	/**
	 * Retrieve the Gross Interval Change
	 * 
	 * @return The Gross Interval Change
	 */

	public double grossChange()
	{
		return grossCleanChange() + _accrualChange;
	}

	/**
	 * Retrieve the Specific Manifest Measure Market Realization Position Change
	 * 
	 * @param manifestMeasure The Manifest Measure
	 * 
	 * @return The Specific Manifest Measure Market Realization Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double specificMarketRealizationChange (
		final String manifestMeasure)
		throws Exception
	{
		PositionManifestMeasureSnap t1PositionManifestMeasureSnap =
			_t1PositionMarketSnap.manifestMeasureSnap (manifestMeasure);

		PositionManifestMeasureSnap t2PositionManifestMeasureSnap =
			_t2PositionMarketSnap.manifestMeasureSnap (manifestMeasure);

		if (null == t1PositionManifestMeasureSnap || null == t2PositionManifestMeasureSnap) {
			throw new Exception (
				"PositionChangeComponents::specificMarketRealizationChange => Invalid Inputs"
			);
		}

		return 0.5 *
			(t1PositionManifestMeasureSnap.sensitivity() + t2PositionManifestMeasureSnap.sensitivity()) *
			(t2PositionManifestMeasureSnap.realization() - t1PositionManifestMeasureSnap.realization());
	}

	/**
	 * Retrieve the Full Manifest Measure Realization Position Change
	 * 
	 * @return The Full Manifest Measure Realization Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double marketRealizationChange()
		throws Exception
	{
		Set<String> manifestMeasureSet = _t1PositionMarketSnap.manifestMeasures();

		if (null == manifestMeasureSet || 0 == manifestMeasureSet.size()) {
			throw new Exception (
				"PositionChangeComponents::marketRealizationChange => No Manifest Measures"
			);
		}

		double marketRealizationChange = 0.;

		for (String manifestMeasure : manifestMeasureSet) {
			marketRealizationChange += specificMarketRealizationChange (manifestMeasure);
		}

		return marketRealizationChange;
	}

	/**
	 * Retrieve the Specific Manifest Measure Market Sensitivity Position Change
	 * 
	 * @param manifestMeasure The Manifest Measure
	 * 
	 * @return The Specific Manifest Measure Market Sensitivity Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double specificMarketSensitivityChange (
		final String manifestMeasure)
		throws Exception
	{
		PositionManifestMeasureSnap t1PositionManifestMeasureSnap =
			_t1PositionMarketSnap.manifestMeasureSnap (manifestMeasure);

		PositionManifestMeasureSnap t2PositionManifestMeasureSnap =
			_t2PositionMarketSnap.manifestMeasureSnap (manifestMeasure);

		if (null == t1PositionManifestMeasureSnap || null == t2PositionManifestMeasureSnap) {
			throw new Exception (
				"PositionChangeComponents::specificMarketSensitivityChange => Invalid Inputs"
			);
		}

		return 0.5 *
			(t1PositionManifestMeasureSnap.realization() + t2PositionManifestMeasureSnap.realization()) *
			(t2PositionManifestMeasureSnap.sensitivity() - t1PositionManifestMeasureSnap.sensitivity());
	}

	/**
	 * Retrieve the Full Manifest Measure Market Sensitivity Position Change
	 * 
	 * @return The Full Manifest Measure Market Sensitivity Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double marketSensitivityChange()
		throws Exception
	{
		Set<String> manifestMeasureSet = _t1PositionMarketSnap.manifestMeasures();

		if (null == manifestMeasureSet || 0 == manifestMeasureSet.size()) {
			throw new Exception (
				"PositionChangeComponents::marketSensitivityChange => No Manifest Measures"
			);
		}

		double marketSensitivityChange = 0.;

		for (String manifestMeasure : manifestMeasureSet) {
			marketSensitivityChange += specificMarketSensitivityChange (manifestMeasure);
		}

		return marketSensitivityChange;
	}

	/**
	 * Retrieve the Specific Manifest Measure Market Roll-down Position Change
	 * 
	 * @param manifestMeasure The Manifest Measure
	 * 
	 * @return The Specific Manifest Measure Market Roll-down Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double specificMarketRollDownChange (
		final String manifestMeasure)
		throws Exception
	{
		PositionManifestMeasureSnap t1PositionManifestMeasureSnap =
			_t1PositionMarketSnap.manifestMeasureSnap (manifestMeasure);

		if (null == t1PositionManifestMeasureSnap) {
			throw new Exception ("PositionChangeComponents::specificMarketRollDownChange => Invalid Inputs");
		}

		return t1PositionManifestMeasureSnap.sensitivity() *
			(t1PositionManifestMeasureSnap.rollDown() - t1PositionManifestMeasureSnap.realization());
	}

	/**
	 * Retrieve the Full Manifest Measure Roll-down Position Change
	 * 
	 * @return The Full Manifest Measure Roll-down Position Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double marketRollDownChange()
		throws Exception
	{
		Set<String> manifestMeasureSet = _t1PositionMarketSnap.manifestMeasures();

		if (null == manifestMeasureSet || 0 == manifestMeasureSet.size()) {
			throw new Exception ("PositionChangeComponents::marketRollDownChange => No Manifest Measures");
		}

		double marketRollDownChange = 0.;

		for (String manifestMeasure : manifestMeasureSet) {
			marketRollDownChange += specificMarketRollDownChange (manifestMeasure);
		}

		return marketRollDownChange;
	}

	/**
	 * Retrieve the Accrual Interval Change
	 * 
	 * @return The Accrual Interval Change
	 */

	public double accrualChange()
	{
		return _accrualChange;
	}

	/**
	 * Retrieve the Explained Interval Change
	 * 
	 * @return The Explained Interval Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double explainedChange()
		throws Exception
	{
		return marketRealizationChange() + marketRollDownChange();
	}

	/**
	 * Retrieve the Unexplained Interval Change
	 * 
	 * @return The Unexplained Interval Change
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public double unexplainedChange()
		throws Exception
	{
		return grossChange() - explainedChange();
	}

	/**
	 * Retrieve the Map of Difference Metrics
	 * 
	 * @return The Map of Difference Metrics
	 */

	public CaseInsensitiveHashMap<Double> differenceMetric()
	{
		return _differenceMetricMap;
	}

	/**
	 * Retrieve the Row of Header Fields
	 * 
	 * @return The Row of Header Fields
	 */

	public String header()
	{
		String header = "FirstDate,SecondDate,TotalPnL,TotalCleanPnL,MarketShiftPnL," +
			"RollDownPnL,AccrualPnL,ExplainedPnL,UnexplainedPnL," + _t1PositionMarketSnap.header ("first") +
			_t2PositionMarketSnap.header ("second");

		if (null == _differenceMetricMap) {
			return header;
		}

		for (String key : _differenceMetricMap.keySet()) {
			header = header + key + "change,";
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
		String content = t1().toString() + "," + t2().toString() + ",";

		content = content + FormatUtil.FormatDouble (grossChange(), 1, 8, 1.) + ",";

		content = content + FormatUtil.FormatDouble (grossCleanChange(), 1, 8, 1.) + ",";

		try {
			content = content + FormatUtil.FormatDouble (marketRealizationChange(), 1, 8, 1.) + ",";

			content = content + FormatUtil.FormatDouble (marketRollDownChange(), 1, 8, 1.) + ",";

			content = content + FormatUtil.FormatDouble (_accrualChange, 1, 8, 1.) + ",";

			content = content + FormatUtil.FormatDouble (explainedChange(), 1, 8, 1.) + ",";

			content = content + FormatUtil.FormatDouble (unexplainedChange(), 1, 8, 1.) + ",";
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		content = content + _t1PositionMarketSnap.content() + _t2PositionMarketSnap.content();

		if (null == _differenceMetricMap) {
			return content;
		}

		for (String key : _differenceMetricMap.keySet()) {
			content = content + FormatUtil.FormatDouble (_differenceMetricMap.get (key), 1, 8, 1.) + ",";
		}

		return content;
	}
}
