
package org.drip.sample.piterbarg2010;

import org.drip.analytics.date.*;
import org.drip.analytics.support.Helper;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.discount.*;

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
 * <i>ForwardContract</i> examines the Valuation of Forward Contract under CSA and non-CSA Settle Agreements.
 * 	CSA is proxied using the OIS Curve, and non-CSA using the Issuer Hedge Funding Curve. The corresponding
 *  Convexity Adjustments using Spread/CSA Covariance are also calculated. The References are:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Barden, P. (2009): Equity Forward Prices in the Presence of Funding Spreads <i>ICBI
 *  			Conference</i> <b>Rome</b>
 *  	</li>
 *  	<li>
 *  		Burgard, C., and M. Kjaer (2009): Modeling and successful Management of Credit Counter-party Risk
 *  			of Derivative Portfolios <i>ICBI Conference</i> <b>Rome</b>
 *  	</li>
 *  	<li>
 *  		Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk <i>Risk</i> <b>20 (2)</b>
 *  			86-90
 *  	</li>
 *  	<li>
 *  		Johannes, M., and S. Sundaresan (2007): Pricing Collateralized Swaps <i>Journal of Finance</i>
 *  			<b>62</b> 383-410
 *  	</li>
 *  	<li>
 *  		Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing
 *  			<i>Risk</i> <b>21 (2)</b> 97-102
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/XVAAnalyticsLibrary.md">XVA Analytics Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/piterbarg2010/README.md">Piterbarg (2010) CSA Measure Extraction</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ForwardContract
{

	private static final DiscountCurve CSACurve (
		final String currency,
		final JulianDate spotDate)
		throws Exception
	{
		String[] depositMaturityTenorArray =
		{
			"1D",
			// "2D",
			// "3D"
		};
		double[] depositQuoteArray =
		{
			0.0004,		// 1D
			// 0.0004,		// 2D
			// 0.0004		// 3D
		};
		String[] shortEndOISMaturityTenorArray =
		{
			"1W",
			"2W",
			"3W",
			"1M"
		};
		double[] shortEndOISQuoteArray =
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};
		String[] oisFuturesEffectiveTenorArray = 
		{
			"1M",
			"2M",
			"3M",
			"4M",
			"5M"
		};
		String[] oisFuturesMaturityTenorArray =
		{
			"1M",
			"1M",
			"1M",
			"1M",
			"1M"
		};
		double[] oisFuturesQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};
		String[] longEndOISMaturityTenorArray =
		{
			"15M",
			"18M",
			"21M",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y"
		};
		double[] longEndOISQuoteArray =
		{
			0.00002,    //  15M
			0.00008,    //  18M
			0.00021,    //  21M
			0.00036,    //   2Y
			0.00127,    //   3Y
			0.00274,    //   4Y
			0.00456,    //   5Y
			0.00647,    //   6Y
			0.00827,    //   7Y
			0.00996,    //   8Y
			0.01147,    //   9Y
			0.01280,    //  10Y
			0.01404,    //  11Y
			0.01516,    //  12Y
			0.01764,    //  15Y
			0.01939,    //  20Y
			0.02003,    //  25Y
			0.02038     //  30Y
		};

		return LatentMarketStateBuilder.SmoothOvernightCurve (
			spotDate,
			currency,
			depositMaturityTenorArray,
			depositQuoteArray,
			"Rate",
			shortEndOISMaturityTenorArray,
			shortEndOISQuoteArray,
			"SwapRate",
			oisFuturesEffectiveTenorArray,
			oisFuturesMaturityTenorArray,
			oisFuturesQuoteArray,
			"SwapRate",
			longEndOISMaturityTenorArray,
			longEndOISQuoteArray,
			"SwapRate"
		);
	}

	private static final DiscountCurve NonCSACurve (
		final String currency,
		final JulianDate spotDate)
		throws Exception
	{
		String[] depositMaturityTenorArray =
		{
			"01D",
			"04D",
			"07D",
			"14D",
			"30D",
			"60D"
		};
		double[] depositQuoteArray =
		{
			0.0013,		//  1D
			0.0017,		//  2D
			0.0017,		//  7D
			0.0018,		// 14D
			0.0020,		// 30D
			0.0023		// 60D
		};
		double[] futuresQuoteArray =
		{
			0.0027,
			0.0032,
			0.0041,
			0.0054,
			0.0077,
			0.0104,
			0.0134,
			0.0160
		};
		String[] fixFloatMaturityTenorArray =
		{
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};
		double[] fixFloatQuoteArray =
		{
			0.0166,		//   4Y
			0.0206,		//   5Y
			0.0241,		//   6Y
			0.0269,		//   7Y
			0.0292,		//   8Y
			0.0311,		//   9Y
			0.0326,		//  10Y
			0.0340,		//  11Y
			0.0351,		//  12Y
			0.0375,		//  15Y
			0.0393,		//  20Y
			0.0402,		//  25Y
			0.0407,		//  30Y
			0.0409,		//  40Y
			0.0409		//  50Y
		};

		return LatentMarketStateBuilder.SmoothFundingCurve (
			spotDate,
			currency,
			depositMaturityTenorArray,
			depositQuoteArray,
			"ForwardRate",
			futuresQuoteArray,
			"ForwardRate",
			fixFloatMaturityTenorArray,
			fixFloatQuoteArray,
			"SwapRate"
		);
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

		String currency = "USD";
		double atmForward = 50.;
		double csaVolatility = 0.1;
		double csaNonCSASpreadVolatility = 0.2;
		double csaNonCSASpreadCorrelation = 0.2;

		JulianDate spotDate = DateUtil.Today().addBusDays (0, currency);

		String[] tenorArray =
		{
			 "1W",
			 "2W",
			 "3W",
			 "1M",
			 "2M",
			 "3M",
			 "6M",
			 "9M",
			"12M",
			"18M",
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			"10Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};

		DiscountCurve overnightCurve = CSACurve (currency, spotDate);

		DiscountCurve fundingCurve = NonCSACurve (currency, spotDate);

		System.out.println();

		System.out.println ("\t||-----------------------------------------------||");

		System.out.println ("\t||     FORWARD CONTRACT CONVEXITY ADJUSTMENT     ||");

		System.out.println ("\t||-----------------------------------------------||");

		System.out.println ("\t|| L -> R:                                       ||");

		System.out.println ("\t||        - Date                                 ||");

		System.out.println ("\t||        - Spread Numeraire                     ||");

		System.out.println ("\t||        - Convexity Adjustment                 ||");

		System.out.println ("\t||        - CSA-Funding Convexity Adjustment     ||");

		System.out.println ("\t||-----------------------------------------------||");

		for (String tenor : tenorArray) {
			JulianDate date = spotDate.addTenor (tenor);

			double tenorToYearFraction = Helper.TenorToYearFraction (tenor);

			double spreadNumeraire = fundingCurve.df (date) / overnightCurve.df (date);

			double convexityAdjustment =
				csaNonCSASpreadCorrelation * csaNonCSASpreadVolatility * csaVolatility;

			System.out.println (
				"\t|| " + date + " | " + FormatUtil.FormatDouble (
					spreadNumeraire,
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					convexityAdjustment * tenorToYearFraction,
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					convexityAdjustment * atmForward * spreadNumeraire * tenorToYearFraction,
					1,
					6,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t||-----------------------------------------------||");

		System.out.println();

		EnvManager.TerminateEnv();
	}
}
