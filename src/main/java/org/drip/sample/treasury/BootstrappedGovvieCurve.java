
package org.drip.sample.treasury;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.definition.Bond;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.TreasuryBuilder;
import org.drip.state.govvie.ExplicitBootGovvieCurve;
import org.drip.state.nonlinear.FlatForwardGovvieCurve;
import org.drip.state.nonlinear.NonlinearCurveBuilder;

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
 * <i>BootstrappedGovvieCurve</i> illustrates Bootstrapping a Yield Curve and recovering the Bond's Yield.
 *  The Reference is:
 *  
 * 	<br><br>
 *  <ul>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/treasury/README.md">G20 Govvie Bond Definitions YAS</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class BootstrappedGovvieCurve
{

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

		JulianDate spotDate = DateUtil.CreateFromYMD (2015, DateUtil.NOVEMBER, 18);

		JulianDate[] effectiveDateArray = new JulianDate[] {
			spotDate,
			spotDate,
			spotDate
		};

		JulianDate[] maturityDateArray = new JulianDate[] {
			spotDate.addTenor ("1Y"),
			spotDate.addTenor ("2Y"),
			spotDate.addTenor ("3Y")
		};

		double[] couponArray = new double[] {
			0.0525,
			0.0525,
			0.0525
		};

		double[] marketYieldArray = new double[] {
			0.035,
			0.040,
			0.045
		};

		String code = "UST";
		String currency = "USD";

		System.out.println (
			"\n\t|-----------------------------------------------------------------------|"
		);

		System.out.println ("\t|                          CALIBRATION SUITE                            |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		System.out.println ("\t|  Inputs => L -> R:                                                    |");

		System.out.println ("\t|    CODE {EFFECTIVE DATE -> MATURITY DATE} COUPON % | MARKET YIELD %   |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		for (int maturityIndex = 0; maturityIndex < maturityDateArray.length; ++maturityIndex) {
			System.out.println (
				"\t| " + code + " {" + effectiveDateArray[maturityIndex] + " -> " +
					maturityDateArray[maturityIndex] + "}" +
					FormatUtil.FormatDouble (couponArray[maturityIndex], 1, 2, 100.) + "% |" +
					FormatUtil.FormatDouble (marketYieldArray[maturityIndex], 1, 2, 100.) + "%"
			);
		}

		System.out.println ("\t|-----------------------------------------------------------------------|");

		int[] dateArray = new int[maturityDateArray.length];
		double[] forwardYieldArray = new double[maturityDateArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityDateArray.length; ++maturityIndex) {
			dateArray[maturityIndex] = maturityDateArray[maturityIndex].julian();

			forwardYieldArray[maturityIndex] = 0.;
		}

		Bond[] bondArray = TreasuryBuilder.FromCode (
			code,
			effectiveDateArray,
			maturityDateArray,
			couponArray
		);

		ValuationParams valuationParams = ValuationParams.Spot (spotDate.julian());

		ExplicitBootGovvieCurve explicitBootGovvieCurve = new FlatForwardGovvieCurve (
			spotDate.julian(),
			code,
			currency,
			dateArray,
			forwardYieldArray
		);

		NonlinearCurveBuilder.YieldCurve (
			valuationParams,
			bondArray,
			marketYieldArray,
			false,
			explicitBootGovvieCurve
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setGovvieState (explicitBootGovvieCurve);

		System.out.println ("\t|  Outputs => L -> R:                                                   |");

		System.out.println ("\t|    FORWARD RATE % | RECALIBRATED MARKET YIELD %                       |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		for (int maturityIndex = 0; maturityIndex < maturityDateArray.length; ++maturityIndex) {
			System.out.println (
				"\t| " + FormatUtil.FormatDouble (
					explicitBootGovvieCurve.yld (dateArray[maturityIndex]),
					1,
					6,
					100.
				) + " % |" + FormatUtil.FormatDouble (
					bondArray[maturityIndex].yieldFromYSpread (
						valuationParams,
						curveSurfaceQuoteContainer,
						null,
						0.
					),
					1,
					6,
					100.
				) + " %"
			);
		}

		System.out.println ("\t|-----------------------------------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
