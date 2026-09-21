
package org.drip.sample.overnight;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.Turn;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableComponent;
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
 * <i>CustomOvernightCurveReconciler</i> demonstrates the multi-stretch transition custom Overnight curve
 *  construction, turns application, discount factor extraction, and calibration quote recovery.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/overnight/README.md">Shape Preserving Stretch Overnight Curve</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomOvernightCurveReconciler
{

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

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				overnightLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			oisArray[maturityIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityIndex],
				couponArray[maturityIndex]
			);
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

	/*
	 * This sample demonstrates the multi-stretch transition custom discount curve construction, turns
	 * 	application, discount factor extraction, and calibration quote recovery. It shows the following
	 * 	steps:
	 * 	- Setup the linear curve calibrator.
	 * 	- Setup the Deposit instruments and their quotes for calibration.
	 * 	- Setup the Deposit instruments stretch latent state representation - this uses the discount factor
	 * 		quantification metric and the "rate" manifest measure.
	 * 	- Setup the OIS instruments and their quotes for calibration.
	 * 	- Setup the OIS instruments stretch latent state representation - this uses the discount factor
	 * 		quantification metric and the "rate" manifest measure.
	 * 	- Calibrate over the instrument set to generate a new overlapping latent state span instance.
	 * 	- Retrieve the "Deposit" stretch from the span.
	 * 	- Retrieve the "OIS" stretch from the span.
	 * 	- Create a discount curve instance by converting the overlapping stretch to an exclusive
	 * 		non-overlapping stretch.
	 * 	- Compare the discount factors and their monotonicity emitted from the discount curve, the
	 * 		non-overlapping span, and the "OIS" stretch across the range of tenor predictor ordinates.
	 * 	- Cross-Recovery of the Deposit Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Cross-Recovery of the OIS Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Create a turn list instance and add new turn instances.
	 * 	- Update the discount curve with the turn list.
	 * 	- Compare the discount factor implied the discount curve with and without applying the turns
	 * 		adjustment.
	 */

	private static final void SplineLinearOISDiscountCurve (
		final JulianDate spotDate,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final String headerComment,
		final String currency)
		throws Exception
	{
		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||  " + headerComment);

		SingleStreamComponent[] depositArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[]
			{
				1,
				2,
				3
			}
		);

		double[] depositQuoteArray =
		{
			0.0004,
			0.0004,
			0.0004		 // Deposit
		};

		double[] shortEndOISQuoteArray =
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

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

		double[] oisFutureQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

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

		CalibratableComponent[] longEndOISArray = OISFromMaturityTenor (
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
					"   DEPOSIT   ",
					depositArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SHORT END OIS",
					shortEndOISArray,
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					" OIS FUTURE  ",
					oisFuturesArray,
					"SwapRate",
					oisFutureQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"LONG END OIS ",
					longEndOISArray,
					"SwapRate",
					longEndOISQuoteArray
				)
			},
			1.,
			valuationParams,
			null,
			null,
			null
		);

		MultiSegmentSequence depositMultiSegmentSequence =
			overlappingStretchSpan.getStretch ("   DEPOSIT   ");

		MultiSegmentSequence oisShortEndMultiSegmentSequence =
			overlappingStretchSpan.getStretch ("SHORT END OIS");

		MultiSegmentSequence oisFutureMultiSegmentSequence =
			overlappingStretchSpan.getStretch (" OIS FUTURE  ");

		MultiSegmentSequence oisLongEndMultiSegmentSequence =
			overlappingStretchSpan.getStretch ("LONG END OIS ");

		MergedDiscountForwardCurve discountCurve =
			new DiscountFactorDiscountCurve (currency, overlappingStretchSpan);

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSITS DF           DFDC       STRETCH        LOCAL");

		System.out.println ("\t||----------------------------------------------------------------");

		int depositWidth = (int) (
			0.25 * (
				depositMultiSegmentSequence.getRightPredictorOrdinateEdge() -
					depositMultiSegmentSequence.getLeftPredictorOrdinateEdge()
			)
		);

		if (0 == depositWidth) {
			depositWidth = 1;
		}

		for (int x = (int) depositMultiSegmentSequence.getLeftPredictorOrdinateEdge();
			x <= (int) depositMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += depositWidth)
		{
			try {
				System.out.println (
					"\t|| DEPOSIT [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
						discountCurve.df (x),
						1,
						8,
						1.
					) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
					FormatUtil.FormatDouble (
						depositMultiSegmentSequence.responseValue (x),
						1,
						8,
						1.
					) + " | " + depositMultiSegmentSequence.monotoneType (x));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||SHORT END OIS DF        DFDC       STRETCH        LOCAL");

		System.out.println ("\t||----------------------------------------------------------------");

		double shortEndOISWidth = 0.2 * (
			oisShortEndMultiSegmentSequence.getRightPredictorOrdinateEdge() -
				oisShortEndMultiSegmentSequence.getLeftPredictorOrdinateEdge()
		);

		for (int x = (int) oisShortEndMultiSegmentSequence.getLeftPredictorOrdinateEdge();
			x <= (int) oisShortEndMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += shortEndOISWidth)
		{
			System.out.println (
				"\t||OIS [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df (x),
					1,
					8,
					1.
				) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
				FormatUtil.FormatDouble (
					oisShortEndMultiSegmentSequence.responseValue (x),
					1,
					8,
					1.
				) + " | " + oisShortEndMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t OIS FUTURE DF          DFDC       STRETCH        LOCAL");

		System.out.println ("\t----------------------------------------------------------------");

		int oisFuturesWidth = (int) (
			0.2 * (
				oisFutureMultiSegmentSequence.getRightPredictorOrdinateEdge() -
					oisFutureMultiSegmentSequence.getLeftPredictorOrdinateEdge()
			)
		);

		for (int x = (int) oisFutureMultiSegmentSequence.getLeftPredictorOrdinateEdge();
			x <= (int) oisFutureMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += oisFuturesWidth)
		{
			System.out.println (
				"\t|| OIS [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df (x),
					1,
					8,
					1.
				) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
				FormatUtil.FormatDouble (
					oisFutureMultiSegmentSequence.responseValue (x),
					1,
					8,
					1.
				) + " | " + oisFutureMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t|| LONG END OIS DF         DFDC      STRETCH         LOCAL");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int x = (int) oisFutureMultiSegmentSequence.getLeftPredictorOrdinateEdge();
			x <= (int) oisFutureMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += oisFuturesWidth)
		{
			System.out.println (
				"\t|| OIS [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df (x),
					1,
					8,
					1.
				) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
				FormatUtil.FormatDouble (
					oisFutureMultiSegmentSequence.responseValue (x),
					1,
					8,
					1.
				) + " | " + oisFutureMultiSegmentSequence.monotoneType (x)
			);
		}

		int longEndOISWidth = (
			(int) oisLongEndMultiSegmentSequence.getRightPredictorOrdinateEdge() -
				(int) oisLongEndMultiSegmentSequence.getLeftPredictorOrdinateEdge()
		) / 10;

		for (int x = (int) oisLongEndMultiSegmentSequence.getLeftPredictorOrdinateEdge() + longEndOISWidth;
			x <= (int) oisLongEndMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += longEndOISWidth)
		{
			System.out.println (
				"\t|| OIS [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df (x),
					1,
					8,
					1.
				) + " || " + overlappingStretchSpan.getContainingStretch (x).name() + " || " +
				FormatUtil.FormatDouble (
					oisLongEndMultiSegmentSequence.responseValue (x),
					1,
					8,
					1.
				) + " | " + oisLongEndMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println (
			"\t|| OIS [" + spotDate.addTenor ("60Y") + "] = " +
			FormatUtil.FormatDouble (discountCurve.df (spotDate.addTenor ("60Y")), 1, 8, 1.)
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams, null,
						MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||      OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int shortEndOISIndex = 0; shortEndOISIndex < shortEndOISArray.length; ++shortEndOISIndex) {
			System.out.println (
				"\t|| [" + shortEndOISArray[shortEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						shortEndOISArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
							null,
							"SwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						shortEndOISQuoteArray[shortEndOISIndex],
						1,
						6,
						1.
					)
				);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||      OIS FUTURES INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesArray.length; ++oisFuturesIndex) {
			System.out.println (
				"\t|| [" + oisFuturesArray[oisFuturesIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						oisFuturesArray[oisFuturesIndex].measureValue (
							valuationParams, null,
							MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
							null,
							"SwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						oisFutureQuoteArray[oisFuturesIndex],
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||      OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t|| [" + longEndOISArray[longEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						longEndOISArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
							null,
							"CalibSwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						longEndOISQuoteArray[longEndOISIndex],
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

		System.out.println ("\t||  TURNS ADJ DF         DFDC");

		System.out.println ("\t||-------------------------------");

		for (int x = (int) oisShortEndMultiSegmentSequence.getLeftPredictorOrdinateEdge();
			x <= (int) oisLongEndMultiSegmentSequence.getRightPredictorOrdinateEdge();
			x += 0.05 * (oisLongEndMultiSegmentSequence.getRightPredictorOrdinateEdge() -
				oisShortEndMultiSegmentSequence.getLeftPredictorOrdinateEdge()))
		{
			System.out.println (
				"\t|| OIS [" + new JulianDate (x) + "] = " + FormatUtil.FormatDouble (
					discountCurve.df (x),
					1,
					8,
					1.
				)
			);
		}
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

		SegmentCustomBuilderControl segmentCustomBuilderControlPolynomial = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
			null
		);

		String currency = "EUR";

		JulianDate today = DateUtil.Today().addTenor ("0D");

		SplineLinearOISDiscountCurve (
			today,
			segmentCustomBuilderControlPolynomial,
			"---- DISCOUNT CURVE WITH OVERNIGHT INDEX ---",
			currency
		);

		EnvManager.TerminateEnv();
	}
}
