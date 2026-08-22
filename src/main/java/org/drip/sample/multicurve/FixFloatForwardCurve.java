
package org.drip.sample.multicurve;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>FixFloatForwardCurve</i> contains the sample demonstrating the full functionality behind creating
 * 	highly customized spline based forward curves from fix-float swaps and the discount curves.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/multicurve/README.md">Multi-Curve Construction and Valuation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixFloatForwardCurve
{

	private static final FixFloatComponent OTCFixFloat (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return IBORFixedFloatContainer.ConventionFromJurisdiction (
			currency,
			"ALL",
			maturityTenor,
			"MAIN"
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final CalibratableComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final int[] maturityDaysArray,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibratableComponentArray =
			new CalibratableComponent[maturityDaysArray.length + futuresCount];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			calibratableComponentArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				ForwardLabel.Create (currency, "3M")
			);
		}

		CalibratableComponent[] futuresArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int componentIndex = maturityDaysArray.length;
			componentIndex < maturityDaysArray.length + futuresCount;
			++componentIndex)
		{
			calibratableComponentArray[componentIndex] =
				futuresArray[componentIndex - maturityDaysArray.length];
		}

		return calibratableComponentArray;
	}

	private static final CalibratableComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int irsIndex = 0; irsIndex < maturityTenorArray.length; ++irsIndex) {
			irsArray[irsIndex] = OTCFixFloat (
				spotDate,
				currency,
				maturityTenorArray[irsIndex],
				couponArray[irsIndex]
			);
		}

		return irsArray;
	}

	private static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency,
		final double bump)
		throws Exception
	{
		double[] swapQuoteArray =
		{
			0.0009875 + bump,   //  9M
			0.0012200 + bump,     //  1Y
			0.0022300 + bump,     // 18M
			0.0038300 + bump,     //  2Y
			0.0082700 + bump,     //  3Y
			0.0124500 + bump,     //  4Y
			0.0160500 + bump,     //  5Y
			0.0259700 + bump      // 10Y
		};

		return ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
			"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
			new ValuationParams (
				spotDate,
				spotDate,
				"USD"
			),
			DepositInstrumentsFromMaturityDays (
				spotDate,
				new int[]
				{
				},
				0,
				currency
			),
			new double[]
			{
			},
			null,
			SwapInstrumentsFromMaturityTenor (
				spotDate,
				currency,
				new String[]
				{
					"9M",
					"1Y",
					"18M",
					"2Y",
					"3Y",
					"4Y",
					"5Y",
					"10Y"
				},
				swapQuoteArray
			),
			swapQuoteArray,
			new String[]
			{
				"SwapRate",		//  9M
				"SwapRate",     //  1Y
				"SwapRate",     // 18M
				"SwapRate",     //  2Y
				"SwapRate",     //  3Y
				"SwapRate",     //  4Y
				"SwapRate",     //  5Y
				"SwapRate"      // 10Y
			},
			true
		);
	}

	private static final FixFloatComponent[] MakeFixFloatxMSwap (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final int tenorInMonths)
		throws Exception
	{
		FixFloatComponent[] fixFloatComponentArray = new FixFloatComponent[maturityTenorArray.length];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			currency,
			true,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			tenorInMonths + "M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			ForwardLabel.Create (currency, tenorInMonths + "M"),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting floatingCompositePeriodSetting = new CompositePeriodSetting (
			12 / tenorInMonths,
			tenorInMonths + "M",
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CompositePeriodSetting fixedCompositePeriodSetting = new CompositePeriodSetting (
			2,
			"6M",
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
			fixFloatComponentArray[maturityIndex] = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"6M",
							maturityTenorArray[maturityIndex],
							null
						),
						fixedCompositePeriodSetting,
						fixedUnitCouponAccrualSetting,
						new ComposableFixedUnitSetting (
							"6M",
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
							tenorInMonths + "M",
							maturityTenorArray[maturityIndex],
							null
						),
						floatingCompositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);
		}

		return fixFloatComponentArray;
	}

	private static final Map<String, ForwardCurve> FixFloatxMBasisSample (
		final JulianDate spotDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final int tenorInMonths,
		final String[] xM6MFwdTenorArray,
		final String manifestMeasure,
		final double[] xM6MBasisSwapQuoteArray,
		final double[] swapCouponArray)
		throws Exception
	{
		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|| SPL =>              n=3              |              n=4               |              KLK               |         |         |"
		);

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------|  LOG DF |  LIBOR  |"
		);

		System.out.println (
			"\t|| MSR =>  RECALC |  REFEREN |  DERIVED |  RECALC  |  REFEREN |  DERIVED |  RECALC  |  REFEREN |  DERIVED |         |         |"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------------------------------|"
		);

		FixFloatComponent[] fixFloatComponentArray = MakeFixFloatxMSwap (
			spotDate,
			currency,
			xM6MFwdTenorArray,
			swapCouponArray,
			tenorInMonths
		);

		String basisTenor = tenorInMonths + "M";

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, basisTenor);

		Map<String, ForwardCurve> forwardCurveMap = new HashMap<String, ForwardCurve>();

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null);

		double startingForward = discountCurve.forward (
			spotDate.julian(),
			spotDate.addTenor (basisTenor).julian()
		);

		ForwardCurve xMCubicPolynomialForwardCurve =
			ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
				"CUBIC_FWD" + basisTenor,
				forwardLabel,
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				fixFloatComponentArray,
				manifestMeasure,
				xM6MBasisSwapQuoteArray,
				startingForward
			);

		CurveSurfaceQuoteContainer cubicPolynomialForwardCurveParameters = MarketParamsBuilder.Create (
			discountCurve,
			xMCubicPolynomialForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		forwardCurveMap.put ("   CUBIC_FWD" + basisTenor, xMCubicPolynomialForwardCurve);

		ForwardCurve xMQuarticPolynomialForwardCurve =
			ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
				"QUARTIC_FWD" + basisTenor,
				ForwardLabel.Create (currency, basisTenor),
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (5),
				fixFloatComponentArray,
				manifestMeasure,
				xM6MBasisSwapQuoteArray,
				startingForward
			);

		CurveSurfaceQuoteContainer quarticPolynomialForwardCurveParameters = MarketParamsBuilder.Create (
			discountCurve,
			xMQuarticPolynomialForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		forwardCurveMap.put (" QUARTIC_FWD" + basisTenor, xMQuarticPolynomialForwardCurve);

		ForwardCurve xMKLKHyperbolicForwardCurve = ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			"KLKHYPER_FWD" + basisTenor,
			ForwardLabel.Create (currency, basisTenor),
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null,
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (1.),
			fixFloatComponentArray,
			manifestMeasure,
			xM6MBasisSwapQuoteArray,
			startingForward
		);

		forwardCurveMap.put ("KLKHYPER_FWD" + basisTenor, xMKLKHyperbolicForwardCurve);

		CurveSurfaceQuoteContainer klkHyperbolicForwardCurveParameters = MarketParamsBuilder.Create (
			discountCurve,
			xMKLKHyperbolicForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		int fixFloatComponentIndex = 0;
		int frequency = 12 / tenorInMonths;

		for (String maturityTenor : xM6MFwdTenorArray) {
			int forwardEndDate = spotDate.addTenor (maturityTenor).julian();

			int forwardStartDate = spotDate.addTenor (maturityTenor).subtractTenor (basisTenor).julian();

			FixFloatComponent fixFloatComponent = fixFloatComponentArray[fixFloatComponentIndex++];

			CaseInsensitiveTreeMap<Double> cubicPolynomialForwardCurveMeasureMap = fixFloatComponent.value (
				valuationParams,
				null,
				cubicPolynomialForwardCurveParameters,
				null
			);

			CaseInsensitiveTreeMap<Double> quarticPolynomialForwardCurveMeasureMap =
				fixFloatComponent.value (
					valuationParams,
					null,
					quarticPolynomialForwardCurveParameters,
					null
				);

			CaseInsensitiveTreeMap<Double> klkHyperbolicForwardCurveMeasureMap = fixFloatComponent.value (
				valuationParams,
				null,
				klkHyperbolicForwardCurveParameters,
				null
			);

			System.out.println (
				"\t|| " + maturityTenor + " =>  " + FormatUtil.FormatDouble (
					xMCubicPolynomialForwardCurve.forward (forwardStartDate),
					2,
					2,
					100.
				) + "  |  " + FormatUtil.FormatDouble (
					cubicPolynomialForwardCurveMeasureMap.get ("ReferenceParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					cubicPolynomialForwardCurveMeasureMap.get ("DerivedParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					xMQuarticPolynomialForwardCurve.forward (forwardStartDate),
					2,
					2,
					100.
				) + "  |  " + FormatUtil.FormatDouble (
					quarticPolynomialForwardCurveMeasureMap.get ("ReferenceParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					quarticPolynomialForwardCurveMeasureMap.get ("DerivedParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					xMKLKHyperbolicForwardCurve.forward (forwardStartDate),
					2,
					2,
					100.
				) + "  |  " + FormatUtil.FormatDouble (
					klkHyperbolicForwardCurveMeasureMap.get ("ReferenceParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					klkHyperbolicForwardCurveMeasureMap.get ("DerivedParBasisSpread"),
					2,
					2,
					1.
				) + "  |  " + FormatUtil.FormatDouble (
					frequency * Math.log (
						discountCurve.df (forwardStartDate) / discountCurve.df (forwardEndDate)
					),
					1,
					2,
					100.
				) + "  |  " +
				FormatUtil.FormatDouble (
					discountCurve.libor (forwardStartDate, forwardEndDate),
					1,
					2,
					100.
				) + "  |  "
			);
		}

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------------------------------|\n"
		);

		return forwardCurveMap;
	}

	private static final Map<String, ForwardCurve> CustomFixFloatForwardCurveSample (
		final JulianDate valuationDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final String calibrationMeasure,
		final int tenorInMonths)
		throws Exception
	{
		return FixFloatxMBasisSample (
			valuationDate,
			currency,
			discountCurve,
			tenorInMonths,
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
			calibrationMeasure,
			new double[]
			{
				0.0005,    //  4Y
				0.0005,    //  5Y
				0.0005,    //  6Y
				0.0005,    //  7Y
				0.0005,    //  8Y
				0.0005,    //  9Y
				0.0005,    // 10Y
				0.0005,    // 11Y
				0.0005,    // 12Y
				0.0005,    // 15Y
				0.0005,    // 20Y
				0.0005,    // 25Y
				0.0005,    // 30Y
				0.0005,    // 40Y
				0.0005     // 50Y
			},
			new double[]
			{
				0.02604,    //  4Y
				0.02808,    //  5Y
				0.02983,    //  6Y
				0.03136,    //  7Y
				0.03268,    //  8Y
				0.03383,    //  9Y
				0.03488,    // 10Y
				0.03583,    // 11Y
				0.03668,    // 12Y
				0.03833,    // 15Y
				0.03854,    // 20Y
				0.03672,    // 25Y
				0.03510,    // 30Y
				0.03266,    // 40Y
				0.03145     // 50Y
			}
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

		MergedDiscountForwardCurve discountCurve = MakeDC (today, currency, 0.);

		CustomFixFloatForwardCurveSample (today, currency, discountCurve, "DerivedParBasisSpread", 3);

		CustomFixFloatForwardCurveSample (today, currency, discountCurve, "ReferenceParBasisSpread", 3);

		EnvManager.TerminateEnv();
	}
}
