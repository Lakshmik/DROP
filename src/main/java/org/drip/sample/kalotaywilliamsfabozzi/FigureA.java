
package org.drip.sample.kalotaywilliamsfabozzi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.municipal.KalotayWilliamsFabozzi;
import org.drip.state.municipal.KalotayWilliamsFabozziPeriodState;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>FigureA</i> reconciles the Output of Figure A in Kalotay, Williams, and Fabozzi (1993). The References
 * 	are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 * 
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/kalotaywilliamsfabozzi/README.md">Kalotay, Williams, Fabozzi (1993) Output Reconcilers</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FigureA
{

	private static final TreeMap<Double, Double> TimeToCalibrationYieldMap()
	{
		TreeMap<Double, Double> timeToCalibrationYieldMap = new TreeMap<Double, Double>();

		timeToCalibrationYieldMap.put (1., 0.03500);

		timeToCalibrationYieldMap.put (2., 0.04010);

		timeToCalibrationYieldMap.put (3., 0.04531);

		return timeToCalibrationYieldMap;
	}

	private static final TreeMap<Double, Double> TimeToProjectedBaseForwardYieldMapInput()
	{
		TreeMap<Double, Double> timeToProjectedBaseForwardYieldMapInput = new TreeMap<Double, Double>();

		timeToProjectedBaseForwardYieldMapInput.put (2., 0.04074);

		timeToProjectedBaseForwardYieldMapInput.put (3., 0.04530);

		return timeToProjectedBaseForwardYieldMapInput;
	}

	private static final Map<Double, List<Double>> TimeToProjectedForwardYieldListMapReconciler()
	{
		Map<Double, List<Double>> timeToProjectedForwardYieldListMap = new TreeMap<Double, List<Double>>();

		List<Double> projected1YForwardYieldList = new ArrayList<Double>();

		projected1YForwardYieldList.add (0.04074);

		projected1YForwardYieldList.add (0.04976);

		timeToProjectedForwardYieldListMap.put (2., projected1YForwardYieldList);

		List<Double> projected2YForwardYieldList = new ArrayList<Double>();

		projected2YForwardYieldList.add (0.04530);

		projected2YForwardYieldList.add (0.05532);

		projected2YForwardYieldList.add (0.06757);

		timeToProjectedForwardYieldListMap.put (3., projected2YForwardYieldList);

		return timeToProjectedForwardYieldListMap;
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double annualizedForwardVolatility = 0.1;

		TreeMap<Double, Double> timeToProjectedBaseForwardYieldMapInput =
			TimeToProjectedBaseForwardYieldMapInput();

		Map<Double, List<Double>> timeToProjectedForwardYieldListMapReconciler =
			TimeToProjectedForwardYieldListMapReconciler();

		KalotayWilliamsFabozzi kalotayWilliamsFabozzi = new KalotayWilliamsFabozzi (
			TimeToCalibrationYieldMap()
		);

		kalotayWilliamsFabozzi.applyProjectedBaseForwardYield (
			annualizedForwardVolatility,
			timeToProjectedBaseForwardYieldMapInput
		);

		TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeToProjectedPeriodStateMap =
			kalotayWilliamsFabozzi.timeToProjectedPeriodStateMap();

		System.out.println ("\t|-----------------------------------------------||");

		System.out.println ("\t| Kalotay, Williams, and Fabozzi (1993) Table 2 ||");

		System.out.println ("\t|-----------------------------------------------||");

		System.out.println ("\t| L -> R:                                       ||");

		System.out.println ("\t|   - Year                                      ||");

		System.out.println ("\t|   - Projected \"Up\" Rate (Reconciler)          ||");

		System.out.println ("\t|-----------------------------------------------||");

		for (double time : timeToProjectedPeriodStateMap.keySet()) {
			if (timeToProjectedForwardYieldListMapReconciler.containsKey (time)) {
				List<KalotayWilliamsFabozziPeriodState> kalotayWilliamsFabozziPeriodStateList =
					timeToProjectedPeriodStateMap.get (time);

				String dump = "\t|" + kalotayWilliamsFabozziPeriodStateList.get (0).period() + " => ";

				List<Double> projectedForwardYieldListReconciliation =
					timeToProjectedForwardYieldListMapReconciler.get (time);

				for (int i = 0; i < kalotayWilliamsFabozziPeriodStateList.size(); ++i) {
					dump += FormatUtil.FormatDouble (
						kalotayWilliamsFabozziPeriodStateList.get (i).forwardYield(),
						1,
						3,
						100.,
						false
					) + "% (" + FormatUtil.FormatDouble (
						projectedForwardYieldListReconciliation.get (i),
						1,
						3,
						100.,
						false
					) + "%) | ";
				}

				System.out.println (dump);
			}
		}

		System.out.println ("\t|-----------------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
