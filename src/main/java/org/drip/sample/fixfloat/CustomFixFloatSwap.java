
package org.drip.sample.fixfloat;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.daycount.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.param.creator.*;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
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
 * <i>CustomFixFloatSwap</i> demonstrates the Construction and Valuation of a Custom Fix-Float Swap.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/fixfloat/README.md">Coupon, Floater, Amortizing IRS Variants</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomFixFloatSwap
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final String floaterTenor,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			floaterTenor,
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			ForwardLabel.Create (currency, floaterTenor),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting compositePeriodSetting = new CompositePeriodSetting (
			Helper.TenorToFreq (floaterTenor),
			floaterTenor,
			currency,
			null,
			1.,
			null,
			null,
			null,
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = new SingleStreamComponent (
				"DEPOSIT_" + maturityDaysArray[maturityIndex],
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.EdgePair (
							effectiveDate,
							effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency)
						),
						compositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			depositArray[maturityIndex].setPrimaryCode (maturityDaysArray[maturityIndex] + "D");
		}

		return depositArray;
	}

	private static final FixFloatComponent CustomIRS (
		final JulianDate effectiveDate,
		final String currency,
		final JulianDate maturityDate,
		final String fixedDayCount,
		final double fixedCoupon,
		final String fixedTenor,
		final String floaterComposableTenor,
		final String floaterCompositeTenor,
		final double notional)
		throws Exception
	{
		return CustomIRS (
			effectiveDate, 
			currency, 
			CompositePeriodBuilder.BackwardEdgeDates (
				effectiveDate,
				maturityDate,
				fixedTenor,
				new DateAdjustParams (Convention.DATE_ROLL_FOLLOWING, 0, currency),
				CompositePeriodBuilder.SHORT_STUB
			),
			CompositePeriodBuilder.BackwardEdgeDates (
				effectiveDate,
				maturityDate,
				floaterCompositeTenor,
				new DateAdjustParams (Convention.DATE_ROLL_FOLLOWING, 0, currency),
				CompositePeriodBuilder.SHORT_STUB
			),
			fixedDayCount,
			fixedCoupon,
			fixedTenor,
			floaterComposableTenor,
			floaterCompositeTenor,
			notional
		);		
	}
	
	private static final FixFloatComponent CustomIRS (
		final JulianDate effectiveDate,
		final String currency,
		final String maturityTenor,
		final String fixedDayCount,
		final double fixedCoupon,
		final String fixedTenor,
		final String floaterComposableTenor,
		final String floaterCompositeTenor,
		final double notional)
		throws Exception
	{
		return CustomIRS (
			effectiveDate, 
			currency, 
			CompositePeriodBuilder.RegularEdgeDates (
				effectiveDate,
				fixedTenor,
				maturityTenor,
				null
			),
			CompositePeriodBuilder.RegularEdgeDates (
				effectiveDate,
				floaterComposableTenor,
				maturityTenor,
				null
			),
			fixedDayCount,
			fixedCoupon,
			fixedTenor,
			floaterComposableTenor,
			floaterCompositeTenor,
			notional
		);		
	}

	private static final FixFloatComponent CustomIRS (
		final JulianDate effectiveDate,
		final String currency,
		List<Integer> fixedStreamEdgeDateList,
		List<Integer> floatingStreamEdgeDateList,
		final String fixedDayCount,
		final double fixedCoupon,
		final String fixedTenor,
		final String floaterComposableTenor,
		final String floaterCompositeTenor,
		final double notional)
		throws Exception
	{
		int fixedFrequency = Helper.TenorToFreq (fixedTenor);

		return new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					fixedStreamEdgeDateList,
					new CompositePeriodSetting (
						fixedFrequency,
						fixedTenor,
						currency,
						null,
						1. * notional,
						null,
						null,
						null,
						null
					),
					new UnitCouponAccrualSetting (
						fixedFrequency,
						fixedDayCount,
						false,
						fixedDayCount,
						false,
						currency,
						false,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						fixedTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						new DateAdjustParams (Convention.DATE_ROLL_FOLLOWING, 0, currency),
						fixedCoupon,
						0.,
						currency
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					floatingStreamEdgeDateList,
					new CompositePeriodSetting (
						Helper.TenorToFreq (floaterCompositeTenor),
						floaterCompositeTenor,
						currency,
						null,
						-1. * notional,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						floaterComposableTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						new DateAdjustParams (Convention.DATE_ROLL_FOLLOWING, 0, currency),
						ForwardLabel.Create (currency, floaterComposableTenor),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			null
		);
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String fixedDayCount,
		final double fixedCoupon,
		final String fixedTenor,
		final String floaterComposableTenor,
		final String floaterCompositeTenor,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			FixFloatComponent irs = CustomIRS (
				effectiveDate, 
				currency, 
				maturityTenorArray[maturityIndex],
				fixedDayCount,
				fixedCoupon,
				fixedTenor,
				floaterComposableTenor,
				floaterCompositeTenor,
				1.
			);		

			irs.setPrimaryCode ("IRS." + maturityTenorArray[maturityIndex] + "." + currency);

			irsArray[maturityIndex] = irs;
		}
		
		return irsArray;
	}

	/*
	 * This sample demonstrates discount curve calibration and input instrument calibration quote recovery.
	 * 	It shows the following:
	 * 	- Construct the Array of Cash/Swap Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Cash/Swap Instrument Set Stretch Builder.
	 * 	- Set up the Linear Curve Calibrator using the following parameters:
	 * 		- Cubic Exponential Mixture Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
	 * 		of Cash and Swap Stretches.
	 * 	- Cross-Comparison of the Cash/Swap Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 */

	private static final void CustomDiscountCurveBuilderSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		System.out.println (
			"\n\t||-------------------------------------------------------------------------------"
		);

		for (Map.Entry<String, Double> measureMapEntry : CustomIRS (
				spotDate.addTenor ("1Y"),
				currency,
				spotDate.addTenor ("11Y"),
				"Act/360",
				0.01,
				"6M",
				"6M",
				"6M",
				1.e6
			).value (
				valuationParams,
				null,
				MarketParamsBuilder.Create (
					ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
						currency,
						new LinearLatentStateCalibrator (
							new SegmentCustomBuilderControl (
								MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
								new PolynomialFunctionSetParams (4),
								SegmentInelasticDesignControl.Create (2, 2),
								new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
								null
							),
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
									"3M",
									new int[]
									{
										1,
										2,
										7,
										14,
										30,
										60
									}
								),
								"ForwardRate",
								new double[]
								{
									0.0013,
									0.0017,
									0.0017,
									0.0018,
									0.0020,
									0.0023
								}
							),
							LatentStateStretchBuilder.ForwardFundingStretchSpec (
								"EDF",
								SingleStreamComponentBuilder.ForwardRateFuturesPack (
									spotDate,
									8,
									currency
								),
								"ForwardRate",
								new double[]
								{
									0.0027,
									0.0032,
									0.0041,
									0.0054,
									0.0077,
									0.0104,
									0.0134,
									0.0160
								}
							),
							LatentStateStretchBuilder.ForwardFundingStretchSpec (
								"SWAP",
								SwapInstrumentsFromMaturityTenor (
									spotDate,
									currency,
									"Act/360",
									0.01,
									"6M",
									"6M",
									"6M",
									new String[]
									{
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
										"30Y",
										"40Y",
										"50Y"
									}
								),
								"SwapRate",
								new double[]
								{
									0.0166,
									0.0206,
									0.0241,
									0.0269,
									0.0292,
									0.0311,
									0.0326,
									0.0340,
									0.0351,
									0.0375,
									0.0393,
									0.0402,
									0.0407,
									0.0409,
									0.0409
								}
							)
						},
						valuationParams,
						null,
						null,
						null,
						1.
					),
					null,
					null,
					null,
					null,
					null,
					null
				),
				null
			).entrySet()
		)
		{
			System.out.println (
				"\t|| " + measureMapEntry.getKey() + " => " +
					FormatUtil.FormatDouble (measureMapEntry.getValue(), 1, 8, 1.) + " |"
			);
		}

		System.out.println (
			"\t||-------------------------------------------------------------------------------"
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

		JulianDate today = DateUtil.Today().addTenor ("0D");

		CustomDiscountCurveBuilderSample (today, currency);

		EnvManager.TerminateEnv();
	}
}
