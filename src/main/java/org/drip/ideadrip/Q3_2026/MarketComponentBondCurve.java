
package org.drip.ideadrip.Q3_2026;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.BondBuilder;
import org.drip.product.credit.BondComponent;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.govvie.MarketComponent;

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
 * <i>MarketComponentBondCurve</i> computes the Govvie Curve from the Set of Market Bond Quotes. The
 * 	References are:
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
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/ideadrip/README.md">IdeaDRIP Publication Data and Methods</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/ideadrip/Q3_2026.md">Municipal Callable Bond Pricing and Curve Construction</a></li>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MarketComponentBondCurve
{

	private static final BondComponent FixedCouponBond (
		final String cusip,
		final double coupon,
		final JulianDate effectiveDate,
		final JulianDate maturityDate)
		throws Exception
	{
		return BondBuilder.CreateSimpleFixed (
			cusip,
			"USD",
			"",
			coupon,
			2,
			"30/360",
			effectiveDate,
			maturityDate,
			null,
			null
		);
	}

	private static final List<MarketComponent> MarketComponentList()
		throws Exception
	{
		List<MarketComponent> marketComponentList = new ArrayList<MarketComponent>();

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"1",
					0.04,
					DateUtil.CreateFromYMD (2025,  2, 02),
					DateUtil.CreateFromYMD (2029,  2, 15)
				),
				0.99908,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"2",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  8,  1)
				),
				1.02160,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"3",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 10, 15)
				),
				1.03519,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"4",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  2, 15)
				),
				1.02500,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"5",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  2, 15)
				),
				1.03139,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"6",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  3,  1)
				),
				1.00997,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"7",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 12,  1)
				),
				1.03406,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"8",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 11,  1)
				),
				1.02299,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"9",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  2, 15)
				),
				1.02717,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"10",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028, 10,  1)
				),
				1.02911,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"11",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028, 12,  1)
				),
				1.01912,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"12",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  2, 15)
				),
				1.02520,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"13",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028, 12,  1)
				),
				1.02300,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"14",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 12,  1)
				),
				1.01390,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"15",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028,  2,  1)
				),
				1.01685,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"16",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2027,  9,  1)
				),
				1.01458,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"17",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 10,  1)
				),
				1.04180,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"18",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028,  9,  1)
				),
				1.02138,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"19",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028, 10,  1)
				),
				1.01613,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"20",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2028,  2, 15)
				),
				1.01566,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"21",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  2, 15)
				),
				0.99308,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"22",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  8,  1)
				),
				1.02160,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"23",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.02393,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"24",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  7, 15)
				),
				1.01789,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"25",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 10, 15)
				),
				1.03519,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"26",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  6, 15)
				),
				1.02508,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"27",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  2, 15)
				),
				1.02500,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"28",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.04367,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"29",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  9, 15)
				),
				1.03830,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"30",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.04428,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"31",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  2, 15)
				),
				1.03139,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"32",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  2, 15)
				),
				1.00930,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"33",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  2, 15)
				),
				1.02911,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"34",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029,  3,  1)
				),
				1.00997,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"35",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 12,  1)
				),
				1.03406,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"36",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  8, 15)
				),
				1.04846,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"37",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  8,  1)
				),
				1.02660,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"38",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2029, 11,  1)
				),
				1.02299,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"39",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  2, 15)
				),
				1.02717,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"40",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030, 11, 15)
				),
				1.01733,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"41",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  2, 15)
				),
				1.04480,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"42",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.02393,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"43",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  9,  1)
				),
				1.06315,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"44",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2030,  7, 15)
				),
				1.01789,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"45",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  8,  1)
				),
				1.06279,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"46",
					0.03,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  8,  1)
				),
				0.91858,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"47",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  2, 15)
				),
				1.04370,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"48",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  6, 15)
				),
				1.02508,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"49",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033, 12,  1)
				),
				0.96755,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"50",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.04367,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"51",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  9, 15)
				),
				1.03830,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"52",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031, 12,  1)
				),
				1.04428,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"53",
					0.03,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033,  8,  1)
				),
				0.90860,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"54",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2032,  8,  1)
				),
				1.04339,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"55",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2032,  6,  1)
				),
				1.04219,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"56",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033, 12,  1)
				),
				0.97556,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"57",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2031,  2, 15)
				),
				1.00930,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"58",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2033, 12,  1)
				),
				1.00840,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"59",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2032,  8,  1)
				),
				1.04178,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"60",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2032,  3,  1)
				),
				1.04380,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"61",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  2,  1)
				),
				1.01599,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"62",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2034,  4,  1)
				),
				1.01605,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"63",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2034, 12,  1)
				),
				1.02507,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"64",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  2, 15)
				),
				1.01913,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"65",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  2,  1)
				),
				1.02910,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"66",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  2, 15)
				),
				1.04250,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"67",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8, 15)
				),
				0.99777,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"68",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8, 15)
				),
				0.99777,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"69",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  3,  1)
				),
				1.04901,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"70",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  2,  1)
				),
				1.02229,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"71",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  2, 15)
				),
				1.04000,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"72",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  6,  1)
				),
				1.04793,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"73",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8,  1)
				),
				1.03608,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"74",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  7,  1)
				),
				1.04596,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"75",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2035,  2,  1)
				),
				1.04502,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"76",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  2, 15)
				),
				0.94301,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"77",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037, 10,  1)
				),
				1.03848,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"78",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8,  1)
				),
				1.01030,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"79",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2034,  8,  1)
				),
				1.03580,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"80",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  8, 15)
				),
				1.01825,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"81",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  2,  1)
				),
				1.01599,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"82",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8, 15)
				),
				0.99777,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"83",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2040,  7,  1)
				),
				0.99500,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"84",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2042,  2, 15)
				),
				1.00098,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"85",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2040,  7,  1)
				),
				1.02153,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"86",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8,  1)
				),
				1.03608,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"87",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  7,  1)
				),
				1.04596,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"88",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2039, 12,  1)
				),
				0.85921,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"89",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041,  6, 15)
				),
				1.00404,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"90",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  2, 15)
				),
				0.94301,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"91",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037, 10,  1)
				),
				1.03848,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"92",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037,  8,  1)
				),
				1.01030,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"93",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038,  8, 15)
				),
				1.01825,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"94",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2037, 12,  1)
				),
				0.91511,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"95",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2039, 12,  1)
				),
				1.00105,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"96",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041, 12,  1)
				),
				1.00679,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"97",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2042,  2, 15)
				),
				1.01206,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"98",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041,  2, 15)
				),
				1.01307,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"99",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2038, 10,  1)
				),
				1.03807,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"100",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041, 12, 15)
				),
				1.02546,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"101",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2043,  2,  1)
				),
				0.87590,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"102",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2040,  7,  1)
				),
				0.99500,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"103",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2042,  2, 15)
				),
				1.00098,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"104",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2040,  7,  1)
				),
				1.02153,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"105",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2044,  2, 15)
				),
				0.86216,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"106",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041,  6, 15)
				),
				1.00404,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"107",
					0.03125,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2044,  8, 15)
				),
				0.74209,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"108",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2044,  6, 15)
				),
				1.03386,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"109",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2044, 10,  1)
				),
				0.90202,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"110",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041, 12,  1)
				),
				1.00679,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"111",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2042,  8,  1)
				),
				1.01398,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"112",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2042,  2, 15)
				),
				1.01206,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"113",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041,  2, 15)
				),
				1.01307,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"114",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041, 12, 15)
				),
				1.02546,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"115",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2043,  4,  1)
				),
				0.98596,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"116",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2043,  8,  1)
				),
				0.91366,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"117",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2040, 12,  1)
				),
				1.03561,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"118",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2041,  8,  1)
				),
				0.89275,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"119",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2044,  8,  1)
				),
				1.00962,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"120",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2046,  8,  1)
				),
				0.86847,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"121",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2048,  2, 15)
				),
				0.99994,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"122",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2049,  6, 15)
				),
				0.80650,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"123",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2046,  8,  1)
				),
				0.86847,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"124",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045,  4,  1)
				),
				0.84923,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"125",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2047,  8,  1)
				),
				0.82634,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"126",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2047,  6,  1)
				),
				0.85130,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"127",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2048,  4,  1)
				),
				1.00144,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"128",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045,  9,  1)
				),
				1.02435,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"129",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2048, 10,  1)
				),
				1.00701,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"130",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045, 10,  1)
				),
				0.90679,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"131",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045,  9, 15)
				),
				0.99835,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"132",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2046,  2,  1)
				),
				1.02589,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"133",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2047,  2,  1)
				),
				0.84149,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"134",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2052,  2,  1)
				),
				0.93173,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"135",
					0.04625,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045,  6, 15)
				),
				1.00419,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"136",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2045,  7,  1)
				),
				1.00733,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"137",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2049,  2, 15)
				),
				1.02120,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"138",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2049,  8, 15)
				),
				0.98927,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"139",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2048,  9, 15)
				),
				0.99268,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"140",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2049,  2, 15)
				),
				1.05177,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"141",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2050,  3, 15)
				),
				0.96043,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"142",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2050,  3, 15)
				),
				0.94408,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"143",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2050,  6, 15)
				),
				0.95368,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"144",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2055, 12,  1)
				),
				0.93455,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"145",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2051,  2, 15)
				),
				0.94711,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"146",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2051,  2, 15)
				),
				0.98621,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"147",
					0.0425,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2054,  2,  1)
				),
				0.80086,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"148",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2053,  2,  1)
				),
				0.96535,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"149",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2052,  8,  1)
				),
				0.95798,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"150",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2054, 12,  1)
				),
				0.94668,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"151",
					0.045,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2052,  5, 15)
				),
				0.84838,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"152",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2055,  6,  1)
				),
				0.94722,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"153",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2050,  8,  1)
				),
				1.00836,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"154",
					0.0525,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2055,  6, 15)
				),
				0.97958,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"155",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2052, 12,  1)
				),
				1.00367,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"156",
					0.045,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2050,  8, 15)
				),
				0.88758,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"157",
					0.0425,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2052,  8,  1)
				),
				0.84874,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"158",
					0.04,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2051,  6,  1)
				),
				0.76770,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"159",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2055,  3, 15)
				),
				0.94590,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"160",
					0.05,
					DateUtil.CreateFromYMD (2025,  2,  2),
					DateUtil.CreateFromYMD (2056,  1,  1)
				),
				1.03206,
				1.
			)
		);

		return marketComponentList;
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

		JulianDate spotDate = DateUtil.CreateFromYMD (2026, DateUtil.SEPTEMBER, 25);

		TreeMap<JulianDate, ArrayList<Double>> marketYieldListMap =
			new TreeMap<JulianDate, ArrayList<Double>>();

		ValuationParams valuationParams = ValuationParams.Spot (spotDate.julian());

		for (MarketComponent marketComponent : MarketComponentList()) {
			BondComponent bond = marketComponent.bond();

			JulianDate maturityDate = bond.maturityDate();

			if (!marketYieldListMap.containsKey (maturityDate)) {
				marketYieldListMap.put (maturityDate, new ArrayList<Double>());
			}

			marketYieldListMap.get (
				maturityDate
			).add (
				bond.yieldFromPrice (valuationParams, null, null, marketComponent.cleanPrice())
			);
		}

		System.out.println();

		System.out.println ("\t||-------------------------||");

		System.out.println ("\t||     MARKET YIELD MAP    ||");

		System.out.println ("\t||-------------------------||");

		for (JulianDate maturityDate : marketYieldListMap.keySet()) {
			String dump = "\t|| [" + maturityDate + "] =>";

			for (double yield : marketYieldListMap.get (maturityDate)) {
				System.out.println (dump + FormatUtil.FormatDouble (yield, 2, 2, 100.) + "% ||");
			}
		}

		System.out.println ("\t||-------------------------||");

		System.out.println();

		EnvManager.TerminateEnv();
	}
}
