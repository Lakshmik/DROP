
package org.drip.sample.funding;

/*
 * Credit Analytics Imports
 */

import org.drip.analytics.date.*;
import org.drip.analytics.daycount.*;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.params.CurrencyPair;
import org.drip.product.rates.*;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.identifier.*;

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
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>MultiStreamSwapMeasures</i> illustrates the creation, invocation, and usage of the MultiStreamSwap. It
 * 	shows how to:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Create the Discount Curve from the rates instruments.
 *  	</li>
 *  	<li>
 *  		Set up the valuation and the market parameters.
 *  	</li>
 *  	<li>
 * 			Create the Rates Basket from the fixed/float streams.
 *  	</li>
 *  	<li>
 * 			Value the Rates Basket.
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

public class MultiStreamSwapMeasures
{

	private static final FixFloatComponent OTCIRS (
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

	private static MergedDiscountForwardCurve BuildRatesCurveFromInstruments (
		final JulianDate startDate,
		final String[] depositTenorArray,
		final double[] depositRateArray,
		final String[] irsTenorArray,
		final double[] irsRateArray,
		final double bump,
		final String currency)
		throws Exception
	{
		int instrumentCount = depositTenorArray.length + irsRateArray.length;
		CalibratableComponent calibratableComponentArray[] = new CalibratableComponent[instrumentCount];
		double componentCalibrationValueArray[] = new double[instrumentCount];
		String calibrationMeasureArray[] = new String[instrumentCount];
		double rateArray[] = new double[instrumentCount];
		int dateArray[] = new int[instrumentCount];

		ComposableFloatingUnitSetting depositComposableFloatingUnitSetting =
			new ComposableFloatingUnitSetting (
				"3M",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
				null,
				ForwardLabel.Create (currency, "3M"),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

		CompositePeriodSetting depositCompositePeriodSetting = new CompositePeriodSetting (
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

		for (int depositIndex = 0; depositIndex < depositTenorArray.length; ++depositIndex) {
			rateArray[depositIndex] = Double.NaN;
			calibrationMeasureArray[depositIndex] = "Rate";
			componentCalibrationValueArray[depositIndex] = depositRateArray[depositIndex] + bump;

			calibratableComponentArray[depositIndex] = new SingleStreamComponent (
				"DEPOSIT_" + depositTenorArray[depositIndex],
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.EdgePair (
							startDate,
							new JulianDate (
								dateArray[depositIndex] =
									startDate.addTenor (depositTenorArray[depositIndex]).julian()
							)
						),
						depositCompositePeriodSetting,
						depositComposableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			calibratableComponentArray[depositIndex].setPrimaryCode (depositTenorArray[depositIndex]);
		}

		for (int irsIndex = 0; irsIndex < irsTenorArray.length; ++irsIndex) {
			rateArray[irsIndex + depositTenorArray.length] = Double.NaN;
			calibrationMeasureArray[irsIndex + depositTenorArray.length] = "Rate";
			componentCalibrationValueArray[irsIndex + depositTenorArray.length] =
				irsRateArray[irsIndex] + bump;

			FixFloatComponent irs = OTCIRS (
				startDate,
				currency,
				irsTenorArray[irsIndex],
				irsRateArray[irsIndex] + bump
			);

			irs.setPrimaryCode ("IRS." + irsTenorArray[irsIndex] + "." + currency);

			calibratableComponentArray[irsIndex + depositTenorArray.length] = irs;
		}

		return ScenarioDiscountCurveBuilder.NonlinearBuild (
			startDate,
			currency,
			calibratableComponentArray,
			componentCalibrationValueArray,
			calibrationMeasureArray,
			null
		);
	}

	private static final RatesBasket MakeRatesBasket (
		final JulianDate effectiveDate)
		throws Exception
	{
		Stream[] fixedStreamArray = new Stream[3];
		Stream[] floatStreamArray = new Stream[3];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			"USD",
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		CompositePeriodSetting fixedCompositePeriodSetting = new CompositePeriodSetting (
			2,
			"6M",
			"USD",
			null,
			1.,
			null,
			null,
			null,
			null
		);

		fixedStreamArray[0] = new Stream (
			CompositePeriodBuilder.FixedCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"3Y",
					null
				),
				fixedCompositePeriodSetting,
				fixedUnitCouponAccrualSetting,
				new ComposableFixedUnitSetting (
					"6M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
					null,
					0.03,
					0.,
					"USD"
				)
			)
		);

		fixedStreamArray[1] = new Stream (
			CompositePeriodBuilder.FixedCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"5Y",
					null
				),
				fixedCompositePeriodSetting,
				fixedUnitCouponAccrualSetting,
				new ComposableFixedUnitSetting (
					"6M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
					null,
					0.05,
					0.,
					"USD"
				)
			)
		);

		fixedStreamArray[2] = new Stream (
			CompositePeriodBuilder.FixedCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"7Y",
					null
				),
				fixedCompositePeriodSetting,
				fixedUnitCouponAccrualSetting,
				new ComposableFixedUnitSetting (
					"6M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
					null,
					0.07,
					0.,
					"USD"
				)
			)
		);

		ForwardLabel forwardLabel = ForwardLabel.Create ("USD", "3M");

		CompositePeriodSetting cpsFloat = new CompositePeriodSetting (
			4,
			"3M",
			"USD",
			null,
			1.,
			null,
			null,
			null,
			null
		);

		floatStreamArray[0] = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"3Y",
					null
				),
				cpsFloat,
				new ComposableFloatingUnitSetting (
					"3M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
					null,
					forwardLabel,
					CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
					0.03
				)
			)
		);

		floatStreamArray[1] = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"5Y",
					null
				),
				cpsFloat,
				new ComposableFloatingUnitSetting (
					"3M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
					null,
					forwardLabel,
					CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
					0.05
				)
			)
		);

		floatStreamArray[2] = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate,
					"6M",
					"7Y",
					null
				),
				cpsFloat,
				new ComposableFloatingUnitSetting (
					"3M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
					null,
					forwardLabel,
					CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
					0.07
				)
			)
		);

		return new RatesBasket ("RATESBASKET", fixedStreamArray, floatStreamArray);
	}

	private static final void MultiLegSwapSample()
		throws Exception
	{
		JulianDate valueDate = DateUtil.Today();

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFundingState (
			BuildRatesCurveFromInstruments (
				valueDate,
				new String[]
				{
					"3M"
				},
				new double[]
				{
					0.00276
				},
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
					"30Y",
					"40Y",
					"50Y"
				},
				new double[]
				{
					0.00367,
					0.00533,
					0.00843,
					0.01238,
					0.01609,
					0.01926,
					0.02191,
					0.02406,
					0.02588,
					0.02741,
					0.02870,
					0.02982,
					0.03208,
					0.03372,
					0.03445,
					0.03484,
					0.03501,
					0.03484
				},
				0.,
				"USD"
			)
		);

		CurrencyPair currencyPair = CurrencyPair.FromCode ("USD/ABC");

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				"FX::" + currencyPair.code(),
				valueDate,
				currencyPair,
				new String[]
				{
					"10Y"
				},
				new double[]
				{
					1.
				},
				1.
			)
		);

		System.out.println (
			MakeRatesBasket (
				valueDate
			).value (
				ValuationParams.Spot (valueDate, 0, "", Convention.DATE_ROLL_ACTUAL),
				null,
				curveSurfaceQuoteContainer,
				null
			)
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

		MultiLegSwapSample();

		EnvManager.TerminateEnv();
	}
}
