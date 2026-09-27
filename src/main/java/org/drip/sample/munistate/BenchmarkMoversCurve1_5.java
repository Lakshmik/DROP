
package org.drip.sample.munistate;

import java.util.ArrayList;
import java.util.List;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.optimization.neldermead.DownhillSimplex;
import org.drip.optimization.neldermead.DownhillSimplexRun;
import org.drip.product.creator.BondBuilder;
import org.drip.product.credit.BondComponent;
import org.drip.sequence.random.BoundedUniform;
import org.drip.service.env.EnvManager;
import org.drip.state.govvie.MarketComponent;
import org.drip.state.govvie.MultiBondCurveCalibrator;

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
 * <i>BenchmarkMoversCurve1_5</i> illustrates the Calibration of the 1-5Y Constant Forward Yield Muni Curve
 * 	using the Specified Suite of Municipal Benchmark Bonds. The References are:
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
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/munistate/README.md">Municipal Curve Construction and Jacobian</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class BenchmarkMoversCurve1_5
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

	private static final void AddVertexList (
		final List<double[]> vertexList,
		final BoundedUniform boundedUniform,
		final int variateCount)
	{
		double[] vertex = new double[variateCount];

		for (int i = 0; i < variateCount; ++i) {
			vertex[i] = boundedUniform.random();
		}

		vertexList.add (vertex);
	}

	private static final List<double[]> VertexList (
		final int variateCount,
		final int vertexCount)
		throws Exception
	{
		List<double[]> vertexList = new ArrayList<double[]>();

		BoundedUniform boundedUniform = new BoundedUniform (0.01, 0.09);

		for (int i = 0; i < vertexCount; ++i) {
			AddVertexList (vertexList, boundedUniform, variateCount);
		}

		return vertexList;
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

		int vertexCount = 10;
		String currency = "USD";

		JulianDate spotDate = DateUtil.CreateFromYMD (2026, DateUtil.SEPTEMBER, 25);

		int[] calibrationDateArray =
		{
			spotDate.addTenor ("1Y").julian(),
			spotDate.addTenor ("2Y").julian(),
			spotDate.addTenor ("3Y").julian(),
			spotDate.addTenor ("4Y").julian(),
			spotDate.addTenor ("5Y").julian()
		};

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

		MultiBondCurveCalibrator multiBondCurveCalibrator = new MultiBondCurveCalibrator (
			spotDate,
			"UST",
			currency,
			calibrationDateArray,
			marketComponentList
		);

		System.out.println (multiBondCurveCalibrator);

		DownhillSimplex downhillSimplex = DownhillSimplex.Standard (
			multiBondCurveCalibrator,
			VertexList (calibrationDateArray.length, vertexCount),
			false,
			false
		);

		DownhillSimplexRun run = downhillSimplex.controlRun();

		System.out.println (run);

		EnvManager.TerminateEnv();
	}
}
