
package org.drip.sample.forward;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.definition.FloaterIndex;
import org.drip.market.otc.*;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.*;
import org.drip.state.inference.*;

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
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * <i>OvernightIndexCurve</i> illustrates the Construction and Usage of the Overnight Index Discount Curve.
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Construct the Merged Forward Discount Curve</li>
 * 		<li>Construct an Elaborate Overnight Discount Curve</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/forward/README.md">IBOR Spline Forward Curve Construction</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OvernightIndexCurve
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray,
		final FloaterIndex floaterIndex)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		ForwardLabel forwardLabel = null == floaterIndex ?
			OvernightLabel.Create (currency) : ForwardLabel.Create (floaterIndex, "ON");

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				forwardLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent OTCOISFixFloat (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return OvernightFixedFloatContainer.FundConventionFromJurisdiction (
			currency
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			oisArray[maturityTenorIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OvernightIndexFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final FloaterIndex floaterIndex)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			currency,
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		ForwardLabel forwardLabel = null == floaterIndex ?
			OvernightLabel.Create (currency) : ForwardLabel.Create (floaterIndex, "ON");

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityIndex],
				"6M"
			) ? maturityTenorArray[maturityIndex] : "6M";

			String floatingTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityIndex],
				"3M"
			) ? maturityTenorArray[maturityIndex] : "3M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							fixedTenor,
							maturityTenorArray[maturityIndex],
							null
						),
						new CompositePeriodSetting (
							2,
							fixedTenor,
							currency,
							null,
							1.,
							null,
							null,
							null,
							null
						),
						fixedUnitCouponAccrualSetting,
						new ComposableFixedUnitSetting (
							fixedTenor,
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
							null,
							couponArray[maturityIndex],
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							floatingTenor,
							maturityTenorArray[maturityIndex],
							null
						),
						new CompositePeriodSetting (
							4,
							floatingTenor,
							currency,
							null,
							-1.,
							null,
							null,
							null,
							null
						),
						new ComposableFloatingUnitSetting (
							"ON",
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
							null,
							forwardLabel,
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
							0.
						)
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[maturityIndex] + "." + currency);

			oisArray[maturityIndex] = ois;
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OISFuturesFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisFuturesArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			oisFuturesArray[maturityIndex] = OTCOISFixFloat (
				spotDate.addTenor (startTenorArray[maturityIndex]),
				currency,
				maturityTenorArray[maturityIndex],
				couponArray[maturityIndex]
			);
		}

		return oisFuturesArray;
	}

	private static final FixFloatComponent[] OvernightIndexFutureFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final FloaterIndex floaterIndex)
		throws Exception
	{
		ForwardLabel forwardLabel = null == floaterIndex ?
			OvernightLabel.Create (currency) : ForwardLabel.Create (floaterIndex, "ON");

		FixFloatComponent[] oisArray = new FixFloatComponent[startTenorArray.length];

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int tenorIndex = 0; tenorIndex < startTenorArray.length; ++tenorIndex) {
			JulianDate effectiveDate = spotDate.addTenor (startTenorArray[tenorIndex]);

			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[tenorIndex],
				"6M"
			) ? maturityTenorArray[tenorIndex] : "6M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"6M",
							maturityTenorArray[tenorIndex],
							null
						),
						new CompositePeriodSetting (
							2,
							fixedTenor,
							currency,
							null,
							1.,
							null,
							null,
							null,
							null
						),
						new UnitCouponAccrualSetting (
							2,
							"Act/360",
							false,
							"Act/360",
							false,
							currency,
							false,
							CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
						),
						new ComposableFixedUnitSetting (
							fixedTenor,
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
							null,
							couponArray[tenorIndex],
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"3M",
							maturityTenorArray[tenorIndex],
							null
						),
						new CompositePeriodSetting (
							4,
							Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
								maturityTenorArray[tenorIndex],
								"3M"
							) ? maturityTenorArray[tenorIndex] : "3M",
							currency,
							null,
							-1.,
							null,
							null,
							null,
							null
						),
						new ComposableFloatingUnitSetting (
							"ON",
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
							null,
							forwardLabel,
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
							0.
						)
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[tenorIndex] + "." + currency);

			oisArray[tenorIndex] = ois;
		}

		return oisArray;
	}

	/**
	 * Construct the Merged Forward Discount Curve
	 * 
	 * @param currency Currency
	 * @param spotDate Spot Date
	 * @param depositMaturityDaysArray Deposit Maturity Days Array
	 * @param depositQuoteArray Deposit Quote Array
	 * @param shortEndOISMaturityTenorArray Short End OIS Maturity Array
	 * @param shortEndOISQuoteArray Short End OIS Quote Array
	 * @param oisFutureTenorArray OIS Tenor Future Array
	 * @param oisFutureMaturityTenorArray OIS Tenor Future Maturity Array
	 * @param oisFutureQuoteArray OIS Tenor Quote Array
	 * @param longEndOISMaturityTenorArray Long End OIS Maturity Array
	 * @param longEndOISQuoteArray Long End OIS Quote Array
	 * @param segmentCustomBuilderControl Segment Custom Builder Control
	 * @param floaterIndex Floater Index
	 * 
	 * @return The Merged Forward Discount Curve
	 * 
	 * @throws Exception Thrown if the Merged Forward Discount Curve cannot be constructed
	 */

	public static final MergedDiscountForwardCurve MakeDC (
		final String currency,
		final JulianDate spotDate,
		final int[] depositMaturityDaysArray,
		final double[] depositQuoteArray,
		final String[] shortEndOISMaturityTenorArray,
		final double[] shortEndOISQuoteArray,
		final String[] oisFutureTenorArray,
		final String[] oisFutureMaturityTenorArray,
		final double[] oisFutureQuoteArray,
		final String[] longEndOISMaturityTenorArray,
		final double[] longEndOISQuoteArray,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final FloaterIndex floaterIndex)
		throws Exception
	{
		CalibratableComponent[] shortEndOISArray = OISFromMaturityTenor (
			spotDate,
			currency,
			new String[]
			{
				"1W",
				"2W",
				"3W",
				"1M"
			},
			shortEndOISQuoteArray
		);

		if (null == shortEndOISArray) {
			shortEndOISArray = OvernightIndexFromMaturityTenor (
				spotDate,
				currency,
				new String[]
				{
					"1W",
					"2W",
					"3W",
					"1M"
				},
				shortEndOISQuoteArray,
				floaterIndex
			);
		}

		CalibratableComponent[] oisFuturesArray = OISFuturesFromMaturityTenor (
			spotDate,
			currency,
			new String[]
			{
				"1M",
				"2M",
				"3M",
				"4M",
				"5M"
			},
			new String[]
			{
				"1M",
				"1M",
				"1M",
				"1M",
				"1M"
			},
			oisFutureQuoteArray
		);

		if (null == oisFuturesArray) {
			oisFuturesArray = OvernightIndexFutureFromMaturityTenor (
				spotDate,
				currency,
				new String[]
				{
					"1M",
					"2M",
					"3M",
					"4M",
					"5M"
				},
				new String[]
				{
					"1M",
					"1M",
					"1M",
					"1M",
					"1M"
				},
				oisFutureQuoteArray,
				floaterIndex
			);
		}

		return ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			new LinearLatentStateCalibrator (
				segmentCustomBuilderControl,
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"DEPOSIT",
					DepositInstrumentsFromMaturityDays (
						spotDate,
						currency,
						depositMaturityDaysArray,
						floaterIndex
					),
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_SHORT_END",
					shortEndOISArray,
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_FUTURE",
					oisFuturesArray,
					"SwapRate",
					oisFutureQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_LONG_END",
					OISFromMaturityTenor (
						spotDate,
						currency,
						new String[]
						{
							"15M",
							"18M",
							"21M",
							"2Y",
							"3Y",
							"4Y",
							"5Y",
							"6Y",
							"7Y",
							"8Y",
							"9Y",
							"10Y",
							"11Y",
							"12Y",
							"15Y",
							"20Y",
							"25Y",
							"30Y"
						},
						longEndOISQuoteArray
					),
					"SwapRate",
					longEndOISQuoteArray
				)
			},
			new ValuationParams (
				spotDate,
				spotDate,
				currency
			),
			null,
			null,
			null,
			1.
		);
	}

	/**
	 * Construct an Elaborate Overnight Discount Curve
	 * 
	 * @param spotDate The Spot Date
	 * @param currency The Currency
	 * 
	 * @return Instance of the Overnight Discount Curve
	 * 
	 * @throws Exception Thrown if the Overnight Discount Curve Could not be created
	 */

	public static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		return MakeDC (
			currency,
			spotDate,
			new int[]
			{
				1,
				2,
				3
			},
			new double[]
			{
				0.0004,	// 1D
				0.0004,	// 2D
				0.0004	// 3D
			},
			new String[]
			{
				"1W",
				"2W",
				"3W",
				"1M"
			},
			new double[]
			{
				0.00070,    //   1W
				0.00069,    //   2W
				0.00078,    //   3W
				0.00074     //   1M
			},
			new String[]
			{
				"1M",
				"1M",
				"1M",
				"1M",
				"1M"
			},
			new String[]
			{
				"1M",
				"2M",
				"3M",
				"4M",
				"5M"
			},
			new double[]
			{
				 0.00046,    //   1M x 1M
				 0.00016,    //   2M x 1M
				-0.00007,    //   3M x 1M
				-0.00013,    //   4M x 1M
				-0.00014     //   5M x 1M
			},
			new String[]
			{
				"15M",
				"18M",
				"21M",
				"2Y",
				"3Y",
				"4Y",
				"5Y",
				"6Y",
				"7Y",
				"8Y",
				"9Y",
				"10Y",
				"11Y",
				"12Y",
				"15Y",
				"20Y",
				"25Y",
				"30Y"
			},
			new double[]
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
			},
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			),
			null
		);
	}
}
