
package org.drip.sample.fixfloat;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
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
 * <i>ShortTenorSwap</i> demonstrates the Construction and Valuation of In-Advance and In-Arrears Short Tenor
 * 	Swap.
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

public class ShortTenorSwap
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray,
		final int referencePeriodType)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"3M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			ForwardLabel.Create (currency, "3M"),
			referencePeriodType,
			0.
		);

		CompositePeriodSetting compositePeriodSetting = new CompositePeriodSetting (
			4,
			"3M",
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

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray,
		final int referencePeriodType,
		final String floatingTenor,
		final String compositeTenor)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			Helper.TenorToFreq (compositeTenor),
			"Act/360",
			false,
			"Act/360",
			false,
			currency,
			true,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			compositeTenor,
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			ForwardLabel.Create (currency, floatingTenor),
			referencePeriodType,
			0.
		);

		ComposableFixedUnitSetting composableFixedUnitSetting = new ComposableFixedUnitSetting (
			compositeTenor,
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			0.,
			0.,
			currency
		);

		CompositePeriodSetting floatingCompositePeriodSetting = new CompositePeriodSetting (
			Helper.TenorToFreq (compositeTenor),
			compositeTenor,
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CompositePeriodSetting fixedCompositePeriodSetting = new CompositePeriodSetting (
			Helper.TenorToFreq (compositeTenor),
			compositeTenor,
			currency,
			null,
			1.,
			null,
			null,
			null,
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			FixFloatComponent irs = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							compositeTenor,
							maturityTenorArray[tenorIndex],
							null
						),
						fixedCompositePeriodSetting,
						fixedUnitCouponAccrualSetting,
						composableFixedUnitSetting
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							compositeTenor,
							maturityTenorArray[tenorIndex],
							null
						),
						floatingCompositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			irs.setPrimaryCode ("IRS." + maturityTenorArray[tenorIndex] + "." + currency);

			irsArray[tenorIndex] = irs;
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
		FixFloatComponent[] inAdvanceIRSArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
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
			},
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			"6M",
			"6M"
		);

		FixFloatComponent[] inAdvanceShortTenorIRSArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
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
			},
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			"12M",
			"3M"
		);

		FixFloatComponent[] inArrearsShortTenorIRSArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
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
			},
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ARREARS,
			"12M",
			"3M"
		);

		double[] swapQuoteArray = new double[]
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
		};

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
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
							new int[]
							{
								1,
								2,
								7,
								14,
								30,
								60
							},
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE
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
						inAdvanceIRSArray,
						"SwapRate",
						swapQuoteArray
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
		);

		System.out.println (
			"\n\t||-------------------------------------------------------------------------------"
		);

		System.out.println ("\t||            IN-ADVANCE/IN-ARREARS SHORT TENOR SWAP METRIC COMPARISON");

		System.out.println (
			"\t||-------------------------------------------------------------------------------"
		);

		System.out.println ("\t||\tL -> R:");

		System.out.println ("\t||\t\t - Swap Maturity");

		System.out.println ("\t||\t\t - In Advance Calibration Quote");

		System.out.println ("\t||\t\t - In Advance Fair Premium");

		System.out.println ("\t||\t\t - In Advance Swap Rate");

		System.out.println ("\t||\t\t - In Advance Short Tenor Swap Rate");

		System.out.println ("\t||\t\t - In Arrears Short Tenor Swap Rate");

		System.out.println ("\t||\t\t - In Advance Short Tenor Swap Rate Shift");

		System.out.println ("\t||\t\t - In Arrears Short Tenor Swap Rate Shift");

		System.out.println (
			"\t||-------------------------------------------------------------------------------"
		);

		for (int irsIndex = 0; irsIndex < inAdvanceIRSArray.length; ++irsIndex) {
			double inAdvanceShortTenorFairPremium = inAdvanceShortTenorIRSArray[irsIndex].measureValue (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				"FairPremium"
			);

			double inArrearsShortTenorFairPremium = inArrearsShortTenorIRSArray[irsIndex].measureValue (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				"FairPremium"
			);

			System.out.println (
				"\t|| [" + inAdvanceIRSArray[irsIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
					inAdvanceIRSArray[irsIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"CalibSwapRate"
					),
					1,
					4,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					swapQuoteArray[irsIndex],
					1,
					4,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					inAdvanceIRSArray[irsIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"FairPremium"
					),
					1,
					4,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					inAdvanceShortTenorFairPremium,
					1,
					4,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					inArrearsShortTenorFairPremium,
					1,
					4,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					inAdvanceShortTenorFairPremium - swapQuoteArray[irsIndex],
					1,
					0,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					inArrearsShortTenorFairPremium - swapQuoteArray[irsIndex],
					1,
					0,
					10000.
				)
			);
		}

		System.out.println (
			"\n\t||-------------------------------------------------------------------------------"
		);

		System.out.println ("\t||            IN-ADVANCE/IN-ARREARS SHORT TENOR SWAP DV01 COMPARISON");

		System.out.println (
			"\t||-------------------------------------------------------------------------------"
		);

		System.out.println ("\t||\tL -> R:");

		System.out.println ("\t||\t\t - Swap Maturity");

		System.out.println ("\t||\t\t - In Advance Swap DV01");

		System.out.println ("\t||\t\t - In Advance Short Tenor Swap DV01");

		System.out.println ("\t||\t\t - In Arrears Short Tenor Swap DV01");

		System.out.println ("\t||\t\t - In Advance Short Tenor Swap DV01 Shift");

		System.out.println ("\t||\t\t - In Arrears Short Tenor Swap DV01 Shift");

		System.out.println (
			"\t||-------------------------------------------------------------------------------"
		);

		for (int irsIndex = 0; irsIndex < inAdvanceIRSArray.length; ++irsIndex) {
			double inAdvanceDV01 = inAdvanceIRSArray[irsIndex].measureValue (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				"FixedDV01"
			);

			double inAdvanceShortTenorDV01 = inAdvanceShortTenorIRSArray[irsIndex].measureValue (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				"FixedDV01"
			);

			double inArrearsShortTenorDV01 = inAdvanceShortTenorIRSArray[irsIndex].measureValue (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				"FixedDV01"
			);

			System.out.println (
				"\t|| [" + inAdvanceIRSArray[irsIndex].maturityDate() + "] =>" +
				FormatUtil.FormatDouble (inAdvanceDV01, 2, 1, 10000.) + " | " +
				FormatUtil.FormatDouble (inAdvanceShortTenorDV01, 2, 1, 10000.) + " | " +
				FormatUtil.FormatDouble (inArrearsShortTenorDV01, 2, 1, 10000.) + " | " +
				FormatUtil.FormatDouble (inAdvanceShortTenorDV01 - inAdvanceDV01, 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (inArrearsShortTenorDV01 - inAdvanceDV01, 1, 2, 10000.)
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
