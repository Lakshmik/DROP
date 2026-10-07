
package org.drip.sample.munistate;

import java.util.ArrayList;
import java.util.List;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.optimization.neldermead.DownhillSimplex;
import org.drip.product.creator.BondBuilder;
import org.drip.product.credit.BondComponent;
import org.drip.service.env.EnvManager;
import org.drip.state.govvie.MarketComponent;
import org.drip.state.govvie.MultiBondLeastSquaresFunction;

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
 * <i>BondBenchmarkCurve1_3</i> illustrates the Calibration of the 1-3Y Constant Forward Yield Muni Curve
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

public class BondBenchmarkCurve1_3
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
		final double f1,
		final double f2,
		final double f3)
	{
		vertexList.add (
			new double[]
			{
				f1,
				f2,
				f3
			}
		);
	}

	private static final List<double[]> VertexList()
	{
		List<double[]> vertexList = new ArrayList<double[]>();

		AddVertexList (vertexList, 0.01, 0.01, 0.01);

		AddVertexList (vertexList, 0.01, 0.01, 0.09);

		AddVertexList (vertexList, 0.01, 0.09, 0.01);

		AddVertexList (vertexList, 0.01, 0.09, 0.09);

		AddVertexList (vertexList, 0.09, 0.01, 0.01);

		AddVertexList (vertexList, 0.09, 0.01, 0.09);

		AddVertexList (vertexList, 0.09, 0.09, 0.01);

		AddVertexList (vertexList, 0.09, 0.09, 0.09);

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

		String currency = "USD";

		JulianDate spotDate = DateUtil.CreateFromYMD (2026, DateUtil.SEPTEMBER, 24);

		int[] calibrationDateArray =
		{
			spotDate.addTenor ("1Y").julian(),
			spotDate.addTenor ("2Y").julian(),
			spotDate.addTenor ("3Y").julian()
		};

		List<MarketComponent> marketComponentList = new ArrayList<MarketComponent>();

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"882854G97",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.OCTOBER, 16),
					DateUtil.CreateFromYMD (2029, DateUtil.OCTOBER, 15)
				),
				1.01687,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"64966SPW0",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.AUGUST, 02),
					DateUtil.CreateFromYMD (2029, DateUtil.AUGUST, 01)
				),
				1.02951,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"20772KH28",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.AUGUST, 16),
					DateUtil.CreateFromYMD (2029, DateUtil.AUGUST, 15)
				),
				1.03540,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"57582THX3",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.JULY, 02),
					DateUtil.CreateFromYMD (2028, DateUtil.JULY, 01)
				),
				1.02505,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"196632R47",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.NOVEMBER, 12),
					DateUtil.CreateFromYMD (2029, DateUtil.NOVEMBER, 11)
				),
				1.03305,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"92778VPX0",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.FEBRUARY, 02),
					DateUtil.CreateFromYMD (2028, DateUtil.FEBRUARY, 01)
				),
				1.01595,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"414008CY6",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.MAY, 16),
					DateUtil.CreateFromYMD (2029, DateUtil.MAY, 15)
				),
				1.01941,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"6775227W8",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.SEPTEMBER, 16),
					DateUtil.CreateFromYMD (2029, DateUtil.SEPTEMBER, 15)
				),
				1.03549,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"373385LT8",
					0.04,
					DateUtil.CreateFromYMD (2025, DateUtil.JULY, 02),
					DateUtil.CreateFromYMD (2028, DateUtil.JULY, 01)
				),
				1.00901,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"262615KX4",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.JUNE, 02),
					DateUtil.CreateFromYMD (2028, DateUtil.JUNE, 01)
				),
				1.02375,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"508336FV1",
					0.04,
					DateUtil.CreateFromYMD (2025, DateUtil.DECEMBER, 01),
					DateUtil.CreateFromYMD (2028, DateUtil.NOVEMBER, 30)
				),
				1.00911,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"677660UZ3",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.JUNE, 02),
					DateUtil.CreateFromYMD (2029, DateUtil.JUNE, 01)
				),
				1.03090,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"13034AYR5",
					0.05,
					DateUtil.CreateFromYMD (2025, DateUtil.OCTOBER, 02),
					DateUtil.CreateFromYMD (2029, DateUtil.OCTOBER, 01)
				),
				1.02340,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"340135LX8",
					0.05,
					DateUtil.CreateFromYMD (2025, 03, 02),
					DateUtil.CreateFromYMD (2028, 03, 01)
				),
				1.02088,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"545149LK0",
					0.05,
					DateUtil.CreateFromYMD (2025, 10, 02),
					DateUtil.CreateFromYMD (2029, 10, 01)
				),
				1.04529,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"235219YB2",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 16),
					DateUtil.CreateFromYMD (2029, 02, 15)
				),
				1.03076,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"011415TL0",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 16),
					DateUtil.CreateFromYMD (2028, 02, 15)
				),
				1.02057,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"704880BQ5",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 16),
					DateUtil.CreateFromYMD (2028, 02, 15)
				),
				1.02292,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"106241G97",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 16),
					DateUtil.CreateFromYMD (2030, 02, 15)
				),
				1.02330,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"93974D2G2",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 02, 01)
				),
				1.01580,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"64972HX98",
					0.05,
					DateUtil.CreateFromYMD (2025, 07, 16),
					DateUtil.CreateFromYMD (2029, 07, 15)
				),
				1.02180,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"882854G89",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 10, 15)
				),
				1.01810,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"13036DPH5",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 04, 01)
				),
				1.03702,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"851035TT2",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 03, 01)
				),
				1.00400,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"544647KH9",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 07, 01)
				),
				1.02802,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"04052ADM5",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 05, 01)
				),
				1.03711,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"27627TCD2",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 07, 01)
				),
				1.01256,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"899525XB0",
					0.03,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 02, 01)
				),
				0.99240,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"6591542Y3",
					0.0525,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 02, 01)
				),
				1.01824,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"727199J85",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 02, 15)
				),
				1.01824,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"30382AQQ8",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2027, 10, 01)
				),
				1.02739,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"574204E90",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2027, 9, 01)
				),
				1.01661,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"13063DB72",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2027, 12, 01)
				),
				1.02964,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"61334PAL7",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 11, 01)
				),
				1.02992,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"495260M99",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2027, 12, 01)
				),
				1.00340,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"45204EX29",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2030, 01, 01)
				),
				1.03106,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"64711RMN3",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 06, 15)
				),
				1.03100,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"106241E32",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2030, 02, 15)
				),
				1.02550,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"232769FF1",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2030, 02, 15)
				),
				1.02679,
				1.
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"79625GBH5",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 02, 01)
				),
				1.01660,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"28282PED2",
					0.08,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 8, 01)
				),
				1.03065,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"574204H71",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 9, 01)
				),
				1.02367,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"574193XP8",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 8, 01)
				),
				1.02648,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"591852Z76",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 12, 01)
				),
				1.03041,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"899607JR7",
					0.04,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 04, 01)
				),
				1.01939,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"677523FB3",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 06, 15)
				),
				1.02612,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"574193TL2",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 8, 01)
				),
				1.03862,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"438687P66",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 07, 01)
				),
				1.02700,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"928110DP7",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 06, 01)
				),
				1.02644,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"544532PA6",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 07, 01)
				),
				1.01907,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"713575WZ7",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 8, 01)
				),
				1.03493,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"97712JFM0",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 10, 01)
				),
				1.03308,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"12008EVY9",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 12, 01)
				),
				1.02456,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"20775YFG6",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 03, 01)
				),
				1.02378,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"235219UB6",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 02, 15)
				),
				1.02713,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"491311AM8",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2029, 02, 01)
				),
				1.01995,
				0.5
			)
		);

		marketComponentList.add (
			new MarketComponent (
				FixedCouponBond (
					"941009CX8",
					0.05,
					DateUtil.CreateFromYMD (2025, 02, 02),
					DateUtil.CreateFromYMD (2028, 05, 01)
				),
				1.01710,
				1.
			)
		);

		System.out.println ("\t||--------------------------------------------------||");

		System.out.println ("\t||         1-3Y BOND BENCHMARK CALIBRATION          ||");

		System.out.println ("\t||--------------------------------------------------||");

		MultiBondLeastSquaresFunction multiBondCurveCalibrator = new MultiBondLeastSquaresFunction (
			spotDate,
			"UST",
			currency,
			calibrationDateArray,
			marketComponentList,
			0
		);

		System.out.print (multiBondCurveCalibrator.toString ("\t|| "));

		System.out.println ("\t||--------------------------------------------------||");

		DownhillSimplex downhillSimplex = DownhillSimplex.Standard (
			multiBondCurveCalibrator,
			VertexList(),
			false,
			false
		);

		System.out.println();

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t|| " + downhillSimplex.controlRun() + " ||");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------||"
		);

		EnvManager.TerminateEnv();
	}
}
