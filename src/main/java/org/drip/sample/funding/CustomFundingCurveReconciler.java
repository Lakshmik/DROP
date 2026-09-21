
package org.drip.sample.funding;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.Turn;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.grid.OverlappingStretchSpan;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.curve.DiscountFactorDiscountCurve;
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
 * <i>CustomFundingCurveReconciler</i> demonstrates the multi-stretch transition custom Funding curve
 * 	construction, turns application, discount factor extraction, and calibration quote recovery. It shows the
 * 	following steps:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Setup the linear curve calibrator.
 *  	</li>
 *  	<li>
 * 			Setup the cash instruments and their quotes for calibration.
 *  	</li>
 *  	<li>
 * 			Setup the cash instruments stretch latent state representation - this uses the discount factor
 * 				quantification metric and the "rate" manifest measure.
 *  	</li>
 *  	<li>
 * 			Setup the swap instruments and their quotes for calibration.
 *  	</li>
 *  	<li>
 * 			Setup the swap instruments stretch latent state representation - this uses the discount factor
 * 				quantification metric and the "rate" manifest measure.
 *  	</li>
 *  	<li>
 * 			Calibrate over the instrument set to generate a new overlapping latent state span instance.
 *  	</li>
 *  	<li>
 * 			Retrieve the "cash" stretch from the span.
 *  	</li>
 *  	<li>
 * 			Retrieve the "swap" stretch from the span.
 *  	</li>
 *  	<li>
 * 			Create a discount curve instance by converting the overlapping stretch to an exclusive
 * 				non-overlapping stretch.
 *  	</li>
 *  	<li>
 * 			Compare the discount factoverlappingStretchSpan and their monotonicity emitted from the discount curve, the
 * 				non-overlapping span, and the "swap" stretch across the range of tenor predictor ordinates.
 *  	</li>
 *  	<li>
 * 			Cross-Recovery of the Cash Calibration Instrument "Rate" metric across the different curve
 * 				construction methodologies.
 *  	</li>
 *  	<li>
 * 			Cross-Recovery of the Swap Calibration Instrument "Rate" metric across the different curve
 * 				construction methodologies.
 *  	</li>
 *  	<li>
 * 			Create a turn list instance and add new turn instances.
 *  	</li>
 *  	<li>
 * 			Update the discount curve with the turn list.
 *  	</li>
 *  	<li>
 * 			Compare the discount factor implied the discount curve with and without applying the turns
 * 				adjustment.
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/funding/README.md">Shape Preserving Local Funding Curve</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomFundingCurveReconciler
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"3M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			ForwardLabel.Create (
				currency,
				"3M"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
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

	private static final FixFloatComponent SwapInstrumentFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final double fixedCoupon,
		final String maturityTenor)
		throws Exception
	{
		FixFloatComponent irs = new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"3M",
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						4,
						"3M",
						currency,
						null,
						1.,
						null,
						null,
						null,
						null
					),
					new UnitCouponAccrualSetting (
						4,
						"Act/360",
						false,
						"Act/360",
						false,
						currency,
						true,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						"3M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						fixedCoupon,
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
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						4,
						"3M",
						currency,
						null,
						-1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						"3M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (currency, "3M"),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new CashSettleParams (0, currency, 0)
		);

		irs.setPrimaryCode ("IRS." + maturityTenor + "." + currency);

		return irs;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			irsArray[maturityIndex] = SwapInstrumentFromMaturityTenor (
				effectiveDate,
				currency,
				0.,
				maturityTenorArray[maturityIndex]
			);
		}

		return irsArray;
	}

	/*
	 * This sample demonstrates the multi-stretch transition custom discount curve construction, turns
	 * 	application, discount factor extraction, and calibration quote recovery. It shows the following
	 * 	steps:
	 * 	- Setup the linear curve calibrator.
	 * 	- Setup the cash instruments and their quotes for calibration.
	 * 	- Setup the cash instruments stretch latent state representation - this uses the discount factor
	 * 		quantification metric and the "rate" manifest measure.
	 * 	- Setup the swap instruments and their quotes for calibration.
	 * 	- Setup the swap instruments stretch latent state representation - this uses the discount factor
	 * 		quantification metric and the "rate" manifest measure.
	 * 	- Calibrate over the instrument set to generate a new overlapping latent state span instance.
	 * 	- Retrieve the "cash" stretch from the span.
	 * 	- Retrieve the "swap" stretch from the span.
	 * 	- Create a discount curve instance by converting the overlapping stretch to an exclusive
	 * 		non-overlapping stretch.
	 * 	- Compare the discount factoverlappingStretchSpan and their monotonicity emitted from the discount curve, the
	 * 		non-overlapping span, and the "swap" stretch across the range of tenor predictor ordinates.
	 * 	- Cross-Recovery of the Cash Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Cross-Recovery of the Swap Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Create a turn list instance and add new turn instances.
	 * 	- Update the discount curve with the turn list.
	 * 	- Compare the discount factor implied the discount curve with and without applying the turns
	 * 		adjustment.
	 */

	private static final void SplineLinearDiscountCurve (
		final JulianDate spotDate,
		final String currency,
		final SegmentCustomBuilderControl segmentCustomBuilderControl)
		throws Exception
	{
		double[] depositQuoteArray =
		{
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		};
		double[] swapQuoteArray =
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

		SingleStreamComponent[] depositArray = DepositInstrumentsFromMaturityDays (
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
			}
		);

		FixFloatComponent[] irsArray = SwapInstrumentsFromMaturityTenor (
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
			}
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		OverlappingStretchSpan overlappingStretchSpan = new LinearLatentStateCalibrator (
			segmentCustomBuilderControl,
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		).calibrateSpan (
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"DEPOSIT",
					depositArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"EDF",
					SingleStreamComponentBuilder.ForwardRateFuturesPack (spotDate, 8, currency),
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
					irsArray,
					"SwapRate",
					swapQuoteArray
				)
			},
			1.,
			valuationParams,
			null,
			null,
			null
		);

		MultiSegmentSequence depositMultiSegmentSequence = overlappingStretchSpan.getStretch ("DEPOSIT");

		MultiSegmentSequence swapMultiSegmentSequence = overlappingStretchSpan.getStretch ("SWAP");

		MergedDiscountForwardCurve discountCurve = new DiscountFactorDiscountCurve (
			currency,
			overlappingStretchSpan
		);

		int rightEdge = (int) depositMultiSegmentSequence.getRightPredictorOrdinateEdge();

		int leftEdge = (int) depositMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT DF            DFDC     STRETCH           LOCAL");

		System.out.println ("\t||----------------------------------------------------------------");

		for (double x = leftEdge; x <= rightEdge; x += 0.1 * (rightEdge - leftEdge)) {
			try {
				System.out.println (
					"\t|| Deposit [" + new JulianDate ((int) x) + "] =>" + FormatUtil.FormatDouble (
						discountCurve.df ((int) x),
						1,
						8,
						1.
					) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
					FormatUtil.FormatDouble (
						depositMultiSegmentSequence.responseValue (x),
						1,
						8,
						1.
					) + " | " + depositMultiSegmentSequence.monotoneType (x)
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		rightEdge = (int) swapMultiSegmentSequence.getRightPredictorOrdinateEdge();

		leftEdge = (int) swapMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SWAP DF            DFDC     STRETCH            LOCAL");

		System.out.println ("\t||----------------------------------------------------------------");

		for (double x = leftEdge; x <= rightEdge; x += 0.1 * (rightEdge - leftEdge)) {
			System.out.println (
				"\t|| Swap [" + new JulianDate ((int) x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df ((int) x),
					1,
					8,
					1.
				) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
				FormatUtil.FormatDouble (
					swapMultiSegmentSequence.responseValue (x),
					1,
					8,
					1.
				) + " | " + swapMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println (
			"\t|| Swap [" + spotDate.addTenor ("60Y") + "] = " + FormatUtil.FormatDouble (
				discountCurve.df (spotDate.addTenor ("60Y")),
				1,
				8,
				1.
			)
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + " |" + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int irsIndex = 0; irsIndex < irsArray.length; ++irsIndex) {
			System.out.println (
				"\t|| [" + irsArray[irsIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
					irsArray[irsIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					swapQuoteArray[irsIndex],
					1,
					6,
					1.
				)
			);
		}

		TurnListDiscountFactor turnListDiscountFactor = new TurnListDiscountFactor();

		turnListDiscountFactor.addTurn (
			new Turn (spotDate.addTenor ("5Y").julian(), spotDate.addTenor ("40Y").julian(), 0.001)
		);

		discountCurve.setTurns (turnListDiscountFactor);

		System.out.println ("\n\t||-------------------------------");

		System.out.println ("\t||     SWAP DF            DFDC");

		System.out.println ("\t||-------------------------------");

		for (double x = leftEdge; x <= rightEdge; x += 0.1 * (rightEdge - leftEdge)) {
			System.out.println (
				"\t|| Swap [" + new JulianDate ((int) x) + "] = " +
					FormatUtil.FormatDouble (discountCurve.df ((int) x), 1, 8, 1.)
			);
		}

		System.out.println ("\t|| -------------------------------");
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

		SplineLinearDiscountCurve (
			DateUtil.Today(),
			"USD",
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			)
		);

		EnvManager.TerminateEnv();
	}
}
