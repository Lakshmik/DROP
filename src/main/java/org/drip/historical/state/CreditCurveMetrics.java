
package org.drip.historical.state;

import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.numerical.common.NumberUtil;

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
 * <i>CreditCurveMetrics</i> holds the computed Metrics associated the Credit Curve State. It provides the
 * 	following Functionality:
 *
 *  <ul>
 * 		<li><i>CreditCurveMetrics</i> Constructor</li>
 * 		<li>Retrieve the Closing Date</li>
 * 		<li>Add the Survival Probability corresponding to the specified Date</li>
 * 		<li>Add the Recovery Rate corresponding to the specified Date</li>
 * 		<li>Retrieve the Survival Probability corresponding to the specified Date</li>
 * 		<li>Retrieve the Recovery Rate corresponding to the specified Date</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/state/README.md">Historical Implied Curve Node Metrics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class CreditCurveMetrics
{
	private JulianDate _closeDate = null;

	private TreeMap<JulianDate, Double> _recoveryRateMap = new TreeMap<JulianDate, Double>();

	private TreeMap<JulianDate, Double> _survivalProbabilityMap = new TreeMap<JulianDate, Double>();

	/**
	 * <i>CreditCurveMetrics</i> Constructor
	 * 
	 * @param closeDate The Closing Date
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public CreditCurveMetrics (
		final JulianDate closeDate)
		throws Exception
	{
		if (null == (_closeDate = closeDate)) {
			throw new Exception ("CreditCurveMetrics Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Closing Date
	 * 
	 * @return The Closing Date
	 */

	public JulianDate close()
	{
		return _closeDate;
	}

	/**
	 * Add the Survival Probability corresponding to the specified Date
	 * 
	 * @param date The Date
	 * @param survivalProbability The Survival Probability
	 * 
	 * @return TRUE - The Dated Survival Probability successfully added
	 */

	public boolean addSurvivalProbability (
		final JulianDate date,
		final double survivalProbability)
	{
		if (null == date || !NumberUtil.IsValid (survivalProbability)) {
			return false;
		}

		_survivalProbabilityMap.put (date, survivalProbability);

		return true;
	}

	/**
	 * Add the Recovery Rate corresponding to the specified Date
	 * 
	 * @param date The Date
	 * @param recoveryRate The Recovery Rate
	 * 
	 * @return TRUE - The Dated Recovery Rate successfully added
	 */

	public boolean addRecoveryRate (
		final JulianDate date,
		final double recoveryRate)
	{
		if (null == date || !NumberUtil.IsValid (recoveryRate)) {
			return false;
		}

		_recoveryRateMap.put (date, recoveryRate);

		return true;
	}

	/**
	 * Retrieve the Survival Probability corresponding to the specified Date
	 * 
	 * @param date The Specified Date
	 * 
	 * @return The corresponding Survival Probability
	 * 
	 * @throws Exception Thrown if the Survival Probability cannot be retrieved
	 */

	public double survivalProbability (
		final JulianDate date)
		throws Exception
	{
		if (null == date || !_survivalProbabilityMap.containsKey (date)) {
			throw new Exception ("CreditCurveMetrics::survivalProbability => Invalid Inputs");
		}

		return _survivalProbabilityMap.get (date);
	}

	/**
	 * Retrieve the Recovery Rate corresponding to the specified Date
	 * 
	 * @param date The Specified Date
	 * 
	 * @return The corresponding Recovery Rate
	 * 
	 * @throws Exception Thrown if the Recovery Rate cannot be retrieved
	 */

	public double recoveryRate (
		final JulianDate date)
		throws Exception
	{
		if (null == date || !_recoveryRateMap.containsKey (date)) {
			throw new Exception ("CreditCurveMetrics::recoveryRate => Invalid Inputs");
		}

		return _recoveryRateMap.get (date);
	}
}
