
package org.drip.sample.fedfund;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.forward.ForwardCurve;
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
 * <i>CompositeFedFundLIBORSwap</i> demonstrates the Construction, the Valuation, and Bloomberg Metrics
 * 	Analysis for the Composite Fed Fund vs. LIBOR Basis Swaps.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/fedfund/README.md">Overnight/Composite Fed Fund LIBOR</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CompositeFedFundLIBORSwap
{

	private static final FloatFloatComponent OTCFloatFloat (
		final JulianDate spotDate,
		final String currency,
		final String derivedTenor,
		final String maturityTenor,
		final double basis)
	{
		return IBORFloatFloatContainer.ConventionFromJurisdiction (
			currency
		).createFloatFloatComponent (
			spotDate,
			derivedTenor,
			maturityTenor,
			basis,
			1.
		);
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

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				OvernightLabel.Create (currency)
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

	private static final MergedDiscountForwardCurve OISDiscountCurve (
		final JulianDate spotDate,
		final String currency,
		final String headerComment)
		throws Exception
	{
		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     " + headerComment);

		System.out.println ("\t----------------------------------------------------------------");

		SingleStreamComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
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

		CalibratableComponent[] shortEndOISComponentArray = OISFromMaturityTenor (
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

		double[] oisFuturesQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		CalibratableComponent[] oisFuturesComponentArray = OISFuturesFromMaturityTenor (
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
			oisFuturesQuoteArray
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

		CalibratableComponent[] longEndOISComponentArray = OISFromMaturityTenor (
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

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
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
					"   DEPOSIT   ",
					depositComponentArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SHORT END OIS",
					shortEndOISComponentArray,
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					" OIS FUTURE  ",
					oisFuturesComponentArray,
					"SwapRate",
					oisFuturesQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"LONG END OIS ",
					longEndOISComponentArray,
					"SwapRate",
					longEndOISQuoteArray
				)
			},
			valuationParams,
			null,
			null,
			null,
			1.
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\t----------------------------------------------------------------");

		System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositComponentArray.length; ++depositIndex) {
			System.out.println (
				"\t[" + depositComponentArray[depositIndex].effectiveDate() + " => " +
					depositComponentArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
						depositComponentArray[depositIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
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

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int shortEndOISIndex = 0;
			shortEndOISIndex < shortEndOISComponentArray.length;
			++shortEndOISIndex)
		{
			Map<String, Double> shortEndOISMeasureMap = shortEndOISComponentArray[shortEndOISIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double calibrationSwapRate = shortEndOISMeasureMap.get ("CalibSwapRate");

			double fairPremium = shortEndOISMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + shortEndOISComponentArray[shortEndOISIndex].effectiveDate() + " => " +
					shortEndOISComponentArray[shortEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (calibrationSwapRate, 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (shortEndOISQuoteArray[shortEndOISIndex], 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (fairPremium - calibrationSwapRate, 1, 2, 10000.)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesComponentArray.length; ++oisFuturesIndex) {
			Map<String, Double> oisFuturesMeasureMap = oisFuturesComponentArray[oisFuturesIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double swapRate = oisFuturesMeasureMap.get ("SwapRate");

			double fairPremium = oisFuturesMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + oisFuturesComponentArray[oisFuturesIndex].effectiveDate() + " => " +
					oisFuturesComponentArray[oisFuturesIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (swapRate, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (oisFuturesQuoteArray[oisFuturesIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium - swapRate, 1, 2, 10000.)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISComponentArray.length; ++longEndOISIndex) {
			Map<String, Double> longEndOISMeasureMap = longEndOISComponentArray[longEndOISIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double calibrationSwapRate = longEndOISMeasureMap.get ("CalibSwapRate");

			double fairPremium = longEndOISMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + longEndOISComponentArray[longEndOISIndex].effectiveDate() + " => " +
					longEndOISComponentArray[longEndOISIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (calibrationSwapRate, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (longEndOISQuoteArray[longEndOISIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium - calibrationSwapRate, 1, 2, 10000.)
			);
		}

		return discountCurve;
	}

	private static final FloatFloatComponent[] MakexM6MBasisSwap (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final int tenorInMonths)
		throws Exception
	{
		String tenor = tenorInMonths + "M";
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			floatFloatComponentArray[maturityIndex] = OTCFloatFloat (
				spotDate,
				currency,
				tenor,
				maturityTenorArray[maturityIndex],
				0.
			);
		}

		return floatFloatComponentArray;
	}

	private static final ForwardCurve MakexMForwardCurve (
		final JulianDate spotDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final int tenorInMonths,
		final String[] xM6MFwdTenorArray,
		final String manifestMeasure,
		final double[] xM6MBasisSwapQuoteArray)
		throws Exception
	{
		String basisTenor = tenorInMonths + "M";

		return ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			"CUBIC_FWD" + basisTenor,
			ForwardLabel.Create (currency, basisTenor),
			new ValuationParams (spotDate, spotDate, currency),
			null,
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
			null,
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			MakexM6MBasisSwap (spotDate, currency, xM6MFwdTenorArray, tenorInMonths),
			manifestMeasure,
			xM6MBasisSwapQuoteArray,
			discountCurve.forward (spotDate.julian(), spotDate.addTenor (basisTenor).julian())
		);
	}

	private static final FloatFloatComponent[] FedFundLIBORBasisSwap (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		ComposableFloatingUnitSetting liborComposableFloatingUnitSetting =
			new ComposableFloatingUnitSetting (
				"3M",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				ForwardLabel.Standard (currency + "-3M"),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

		ComposableFloatingUnitSetting fedFundComposableFloatingUnitSetting =
			new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				OvernightLabel.Create (currency),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

		CompositePeriodSetting liborCompositePeriodSetting = new CompositePeriodSetting (
			4,
			"3M",
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CompositePeriodSetting fedFundCompositePeriodSetting = new CompositePeriodSetting (
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

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			floatFloatComponentArray[maturityIndex] = new FloatFloatComponent (
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"3M",
							maturityTenorArray[maturityIndex],
							null
						),
						liborCompositePeriodSetting,
						liborComposableFloatingUnitSetting
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"3M",
							maturityTenorArray[maturityIndex],
							null
						),
						fedFundCompositePeriodSetting,
						fedFundComposableFloatingUnitSetting
					)
				),
				cashSettleParams
			);
		}

		return floatFloatComponentArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
		ForwardLabel forwardLabel = ForwardLabel.Create (currency, "3M");

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
							"3M",
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
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

		String[] maturityTenorArray =
		{
			"1Y",
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
		};

		JulianDate today = DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11);

		MergedDiscountForwardCurve oisDiscountCurve = OISDiscountCurve (
			today,
			currency,
			"OVERNIGHT INDEX RUN RECONCILIATION"
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			oisDiscountCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		curveSurfaceQuoteContainer.setForwardState (
			MakexMForwardCurve (
				today,
				currency,
				oisDiscountCurve,
				3,
				new String[]
				{
					"1Y",
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
				"ReferenceParBasisSpread",
				new double[]
				{
					0.00186,    //  1Y
					0.00127,    //  2Y
					0.00097,    //  3Y
					0.00080,    //  4Y
					0.00067,    //  5Y
					0.00058,    //  6Y
					0.00051,    //  7Y
					0.00046,    //  8Y
					0.00042,    //  9Y
					0.00038,    // 10Y
					0.00035,    // 11Y
					0.00033,    // 12Y
					0.00028,    // 15Y
					0.00022,    // 20Y
					0.00020,    // 25Y
					0.00018     // 30Y
				}
			)
		);

		FloatFloatComponent[] fedFundLIBORSwapArray = FedFundLIBORBasisSwap (
			today,
			currency,
			maturityTenorArray
		);

		FixFloatComponent[] oisArray = OISFromMaturityTenor (
			today,
			currency,
			maturityTenorArray,
			new double[]
			{
				0.00002,
				0.00036,
				0.00127,
				0.00274,
				0.00456,
				0.00647,
				0.00827,
				0.00996,
				0.01147,
				0.01280,
				0.01404,
				0.01516,
				0.01764,
				0.01939,
				0.02003,
				0.02038
			}
		);

		FixFloatComponent[] irsArray = SwapInstrumentsFromMaturityTenor (
			today,
			maturityTenorArray,
			new double[]
			{
				0.00002,
				0.00036,
				0.00127,
				0.00274,
				0.00456,
				0.00647,
				0.00827,
				0.00996,
				0.01147,
				0.01280,
				0.01404,
				0.01516,
				0.01764,
				0.01939,
				0.02003,
				0.02038
			},
			currency
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		System.out.println ("\n\t--------------------------------------------------------------------------");

		System.out.println ("\t                    FED FUND OIS BASIS COMPARISON");

		System.out.println ("\t--------------------------------------------------------------------------");

		System.out.println ("\t\tOutput Order[Effective Date - Maturity Date]");

		System.out.println ("\t\t\t IRS Rate (%)");

		System.out.println ("\t\t\t Fed Fund LIBOR Basis (bp)");

		System.out.println ("\t\t\t OIS Rate Uncompounded (%) (Bloomberg 2010 Methodology)");

		System.out.println ("\t\t\t OIS Rate Daily Compounded (%) (Bloomberg 2010 Methodology)");

		System.out.println ("\t\t\t OIS Rate (%) From Full Calibration\n");

		System.out.println ("\t--------------------------------------------------------------------------");

		for (int i = 0; i < fedFundLIBORSwapArray.length; ++i) {
			double oisRate = oisArray[i].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			).get (
				"SwapRate"
			);

			double irsRate = irsArray[i].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			).get (
				"SwapRate"
			);

			double liborFedFundBasis = irsRate - oisRate;

			System.out.println (
				"\t[" + fedFundLIBORSwapArray[i].effectiveDate() + " - " +
					fedFundLIBORSwapArray[i].maturityDate() + "] => " + FormatUtil.FormatDouble (
						irsRate,
						1,
						4,
						100.
					) + "% | " + FormatUtil.FormatDouble (
						liborFedFundBasis,
						1,
						1,
						10000.
					) + " | " + FormatUtil.FormatDouble (
						Helper.OISFromLIBORSwapFedFundBasis (irsRate, -liborFedFundBasis),
						1,
						4,
						100.
					) + "% | " + FormatUtil.FormatDouble (
						Helper.OISFromLIBORSwapFedFundBasis2 (irsRate, -liborFedFundBasis),
						1,
						4,
						100.
					) + "% | " + FormatUtil.FormatDouble (
						oisRate,
						1,
						4,
						100.
					) + "%"
			);
		}

		System.out.println ("\t--------------------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
