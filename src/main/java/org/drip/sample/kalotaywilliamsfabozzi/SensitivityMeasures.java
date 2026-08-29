
package org.drip.sample.kalotaywilliamsfabozzi;

import java.util.TreeMap;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.municipal.KalotayWilliamsFabozzi;
import org.drip.state.municipal.ProxyBond;
import org.drip.state.municipal.ProxyBondSensitivity;

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
 * <i>SensitivityMeasures</i> illustrates the Computation of the Muni Bond's Sensitivity from Yield Spreads
 * 	using the Grid Layout in Kalotay, Williams, and Fabozzi (1993). The References are:
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

public class SensitivityMeasures
{

	private static final TreeMap<Double, Double> TimeToCalibrationYieldMapInput()
	{
		TreeMap<Double, Double> timeToCalibrationYieldMap = new TreeMap<Double, Double>();

		timeToCalibrationYieldMap.put (1., 0.02523);

		timeToCalibrationYieldMap.put (2., 0.02595);

		timeToCalibrationYieldMap.put (3., 0.02673);

		timeToCalibrationYieldMap.put (4., 0.02758);

		timeToCalibrationYieldMap.put (5., 0.02848);

		timeToCalibrationYieldMap.put (6., 0.02944);

		timeToCalibrationYieldMap.put (7., 0.03044);

		timeToCalibrationYieldMap.put (8., 0.03148);

		timeToCalibrationYieldMap.put (9., 0.03255);

		timeToCalibrationYieldMap.put (10., 0.03365);

		timeToCalibrationYieldMap.put (11., 0.03477);

		timeToCalibrationYieldMap.put (12., 0.03589);

		timeToCalibrationYieldMap.put (13., 0.03699);

		timeToCalibrationYieldMap.put (14., 0.03806);

		timeToCalibrationYieldMap.put (15., 0.03905);

		timeToCalibrationYieldMap.put (16., 0.03998);

		timeToCalibrationYieldMap.put (17., 0.04082);

		timeToCalibrationYieldMap.put (18., 0.04158);

		timeToCalibrationYieldMap.put (19., 0.04228);

		timeToCalibrationYieldMap.put (20., 0.04290);

		timeToCalibrationYieldMap.put (21., 0.04346);

		timeToCalibrationYieldMap.put (22., 0.04396);

		timeToCalibrationYieldMap.put (23., 0.04440);

		timeToCalibrationYieldMap.put (24., 0.04478);

		timeToCalibrationYieldMap.put (25., 0.04511);

		timeToCalibrationYieldMap.put (26., 0.04539);

		timeToCalibrationYieldMap.put (27., 0.04562);

		timeToCalibrationYieldMap.put (28., 0.04581);

		timeToCalibrationYieldMap.put (29., 0.04596);

		timeToCalibrationYieldMap.put (30., 0.04608);

		return timeToCalibrationYieldMap;
	}

	private static final TreeMap<Double, Double> TimeToYieldBasisMap()
	{
		TreeMap<Double, Double> timeToYieldBasisMap = new TreeMap<Double, Double>();

		timeToYieldBasisMap.put ( 1., 17.0);

		timeToYieldBasisMap.put ( 2., 19.0);

		timeToYieldBasisMap.put ( 3., 21.9);

		timeToYieldBasisMap.put ( 4., 21.7);

		timeToYieldBasisMap.put ( 5., 23.3);

		timeToYieldBasisMap.put ( 6., 25.5);

		timeToYieldBasisMap.put ( 7., 28.0);

		timeToYieldBasisMap.put ( 8., 30.8);

		timeToYieldBasisMap.put ( 9., 34.1);

		timeToYieldBasisMap.put (10., 38.0);

		timeToYieldBasisMap.put (11., 48.5);

		timeToYieldBasisMap.put (12., 53.7);

		timeToYieldBasisMap.put (13., 60.0);

		timeToYieldBasisMap.put (14., 65.8);

		timeToYieldBasisMap.put (15., 70.9);

		timeToYieldBasisMap.put (16., 75.7);

		timeToYieldBasisMap.put (17., 80.2);

		timeToYieldBasisMap.put (18., 84.0);

		timeToYieldBasisMap.put (19., 87.5);

		timeToYieldBasisMap.put (20., 90.8);

		timeToYieldBasisMap.put (21., 95.2);

		timeToYieldBasisMap.put (22., 97.3);

		timeToYieldBasisMap.put (23., 100.2);

		timeToYieldBasisMap.put (24., 102.6);

		timeToYieldBasisMap.put (25., 105.1);

		/* timeToYieldBasisMap.put (26., 106.9);

		timeToYieldBasisMap.put (27., 108.3);

		timeToYieldBasisMap.put (28., 109.3);

		timeToYieldBasisMap.put (29., 109.8);

		timeToYieldBasisMap.put (30., 110.1); */

		return timeToYieldBasisMap;
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

		double bondCoupon = 0.05;
		double annualizedForwardYieldVolatility = 0.1;

		TreeMap<Double, Double> callPriceSchedule = new TreeMap<Double, Double>();

		callPriceSchedule.put (10., 1.);

		KalotayWilliamsFabozzi kalotayWilliamsFabozzi =
			new KalotayWilliamsFabozzi (TimeToCalibrationYieldMapInput());

		kalotayWilliamsFabozzi.applyProjectedBaseForwardYield (
			annualizedForwardYieldVolatility,
			KalotayWilliamsFabozzi.BASE_FORWARD_NODE_ADJUSTED_CUMULATIVE_YIELD
		);

		TreeMap<Double, Double> timeToYieldBasisMap = TimeToYieldBasisMap();

		System.out.println ("\t||----------------------------------------------------||");

		System.out.println ("\t||           OAS BASED SENSITIVITY MEASURES           ||");

		System.out.println ("\t||----------------------------------------------------||");

		System.out.println ("\t||  L -> R:                                           ||");

		System.out.println ("\t||      - Theoretical Price                           ||");

		System.out.println ("\t||      - Market Price                                ||");

		System.out.println ("\t||      - Duration                                    ||");

		System.out.println ("\t||      - Convexity                                   ||");

		System.out.println ("\t||      - Yield Basis        (bp)                     ||");

		System.out.println ("\t||----------------------------------------------------||");

		for (Double endTime : timeToYieldBasisMap.keySet()) {
			ProxyBond proxyBond = ProxyBond.Callable (endTime, bondCoupon, callPriceSchedule);

			ProxyBondSensitivity proxyBondSensitivity = proxyBond.secularMeasuresFromYieldBasis (
				kalotayWilliamsFabozzi,
				annualizedForwardYieldVolatility,
				0.0001 * timeToYieldBasisMap.get (endTime)
			);

			System.out.println (
				"\t|| " + FormatUtil.FormatDouble (
					endTime,
					2,
					0,
					1.
				) + " =>" + FormatUtil.FormatDouble (
					proxyBondSensitivity.theoreticalPrice(),
					3,
					4,
					100.
				) + " |" + FormatUtil.FormatDouble (
					proxyBondSensitivity.marketPrice(),
					3,
					4,
					100.
				) + " |" + FormatUtil.FormatDouble (
					proxyBondSensitivity.duration(),
					2,
					2,
					1.
				) + " |" + FormatUtil.FormatDouble (
					proxyBondSensitivity.convexity(),
					3,
					1,
					1.
				) + " |" + FormatUtil.FormatDouble (
					proxyBondSensitivity.yieldBasis(),
					3,
					1,
					10000.
				) + " ||"
			);
		}

		System.out.println ("\t||----------------------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
