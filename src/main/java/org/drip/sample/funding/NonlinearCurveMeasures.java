
package org.drip.sample.funding;

import org.drip.analytics.date.DateUtil;

/*
 * Credit Product imports
 */

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.numerical.differentiation.WengertJacobian;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
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
 * <i>NonlinearCurveMeasures</i> contains a demo of the Non-linear Rates Analytics API Usage. It shows the
 * 	following:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Build a discount curve using: cash instruments only, EDF instruments only, IRS instruments only,
 * 				or all of them strung together.
 *  	</li>
 *  	<li>
 * 			Re-calculate the component input measure quotes from the calibrated discount curve object.
 *  	</li>
 *  	<li>
 * 			Compute the PVDF Wengert Jacobian across all the instruments used in the curve construction.
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

public class NonlinearCurveMeasures
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

	private static void DiscountCurveFromRatesInstruments()
		throws Exception
	{
		int NUM_DC_INSTR = 30;
		double rateArray[] = new double[NUM_DC_INSTR];
		int maturityDateArray[] = new int[NUM_DC_INSTR];
		String calibrationMeasureArray[] = new String[NUM_DC_INSTR];
		double componentCalibrationValueArray[] = new double[NUM_DC_INSTR];
		CalibratableComponent calibratableComponentArray[] = new CalibratableComponent[NUM_DC_INSTR];

		JulianDate startDate = DateUtil.CreateFromYMD (2011, 4, 6);

		JulianDate cashEffectiveDateArray = startDate.addBusDays (1, "USD");

		maturityDateArray[0] = cashEffectiveDateArray.addBusDays (1, "USD").julian(); // ON

		maturityDateArray[1] = cashEffectiveDateArray.addBusDays (2, "USD").julian(); // 1D (TN)

		maturityDateArray[2] = cashEffectiveDateArray.addBusDays (7, "USD").julian(); // 1W

		maturityDateArray[3] = cashEffectiveDateArray.addBusDays (14, "USD").julian(); // 2W

		maturityDateArray[4] = cashEffectiveDateArray.addBusDays (30, "USD").julian(); // 1M

		maturityDateArray[5] = cashEffectiveDateArray.addBusDays (60, "USD").julian(); // 2M

		maturityDateArray[6] = cashEffectiveDateArray.addBusDays (90, "USD").julian(); // 3M

		componentCalibrationValueArray[0] = 0.0013;
		componentCalibrationValueArray[1] = 0.0017;
		componentCalibrationValueArray[2] = 0.0017;
		componentCalibrationValueArray[3] = 0.0018;
		componentCalibrationValueArray[4] = 0.0020;
		componentCalibrationValueArray[5] = 0.0023;
		componentCalibrationValueArray[6] = 0.0026;

		ForwardLabel forwardLabel = ForwardLabel.Create ("USD", "3M");

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"3M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			forwardLabel,
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting compositePeriodSetting = new CompositePeriodSetting (
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

		CashSettleParams cashSettleParams = new CashSettleParams (0, "USD", 0);

		for (int i = 0; i < 7; ++i) {
			rateArray[i] = 0.01;
			calibrationMeasureArray[i] = "Rate";

			calibratableComponentArray[i] = SingleStreamComponentBuilder.Deposit (
				cashEffectiveDateArray, // Effective
				new JulianDate (maturityDateArray[i]).addBusDays (2, "USD"), // Maturity
				forwardLabel
			);

			calibratableComponentArray[i] = new SingleStreamComponent (
				"DEPOSIT_" + maturityDateArray[i],
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.EdgePair (
							startDate,
							new JulianDate (maturityDateArray[i]).addBusDays (2, "USD")
						),
						compositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			calibratableComponentArray[i].setPrimaryCode (calibratableComponentArray[i].name());
		}

		componentCalibrationValueArray[7] = 0.0027;
		componentCalibrationValueArray[8] = 0.0032;
		componentCalibrationValueArray[9] = 0.0041;
		componentCalibrationValueArray[10] = 0.0054;
		componentCalibrationValueArray[11] = 0.0077;
		componentCalibrationValueArray[12] = 0.0104;
		componentCalibrationValueArray[13] = 0.0134;
		componentCalibrationValueArray[14] = 0.0160;

		CalibratableComponent[] futuresArray =
			SingleStreamComponentBuilder.ForwardRateFuturesPack (startDate, 8, "USD");

		for (int i = 0; i < 8; ++i) {
			rateArray[i + 7] = 0.01;
			calibrationMeasureArray[i + 7] = "Rate";
			calibratableComponentArray[i + 7] = futuresArray[i];

			maturityDateArray[i + 7] = futuresArray[i].maturityDate().julian();
		}

		JulianDate irsEffectiveDate = startDate.addBusDays (2, "USD");

		String[] irsTenorArray =
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
			"50Y",
		};

		maturityDateArray[15] = irsEffectiveDate.addTenor (irsTenorArray[0]).julian();

		maturityDateArray[16] = irsEffectiveDate.addTenor (irsTenorArray[1]).julian();

		maturityDateArray[17] = irsEffectiveDate.addTenor (irsTenorArray[2]).julian();

		maturityDateArray[18] = irsEffectiveDate.addTenor (irsTenorArray[3]).julian();

		maturityDateArray[19] = irsEffectiveDate.addTenor (irsTenorArray[4]).julian();

		maturityDateArray[20] = irsEffectiveDate.addTenor (irsTenorArray[5]).julian();

		maturityDateArray[21] = irsEffectiveDate.addTenor (irsTenorArray[6]).julian();

		maturityDateArray[22] = irsEffectiveDate.addTenor (irsTenorArray[7]).julian();

		maturityDateArray[23] = irsEffectiveDate.addTenor (irsTenorArray[8]).julian();

		maturityDateArray[24] = irsEffectiveDate.addTenor (irsTenorArray[9]).julian();

		maturityDateArray[25] = irsEffectiveDate.addTenor (irsTenorArray[10]).julian();

		maturityDateArray[26] = irsEffectiveDate.addTenor (irsTenorArray[11]).julian();

		maturityDateArray[27] = irsEffectiveDate.addTenor (irsTenorArray[12]).julian();

		maturityDateArray[28] = irsEffectiveDate.addTenor (irsTenorArray[13]).julian();

		maturityDateArray[29] = irsEffectiveDate.addTenor (irsTenorArray[14]).julian();

		componentCalibrationValueArray[15] = .0166;
		componentCalibrationValueArray[16] = .0206;
		componentCalibrationValueArray[17] = .0241;
		componentCalibrationValueArray[18] = .0269;
		componentCalibrationValueArray[19] = .0292;
		componentCalibrationValueArray[20] = .0311;
		componentCalibrationValueArray[21] = .0326;
		componentCalibrationValueArray[22] = .0340;
		componentCalibrationValueArray[23] = .0351;
		componentCalibrationValueArray[24] = .0375;
		componentCalibrationValueArray[25] = .0393;
		componentCalibrationValueArray[26] = .0402;
		componentCalibrationValueArray[27] = .0407;
		componentCalibrationValueArray[28] = .0409;
		componentCalibrationValueArray[29] = .0409;

		for (int i = 0; i < 15; ++i) {
			rateArray[i + 15] = 0.01;
			calibrationMeasureArray[i + 15] = "Rate";

			calibratableComponentArray[i + 15] = OTCIRS (irsEffectiveDate, "USD", irsTenorArray[i], 0.);
		}

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.NonlinearBuild (
			startDate,
			"USD",
			calibratableComponentArray,
			componentCalibrationValueArray,
			calibrationMeasureArray,
			null
		);

		ValuationParams valuationParams = new ValuationParams (startDate, startDate, "USD");

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null);

		for (int componentIndex = 0; componentIndex < calibratableComponentArray.length; ++componentIndex) {
			System.out.println (
				"\t|| " +calibrationMeasureArray[componentIndex] + "[" + componentIndex + "] =>" +
				FormatUtil.FormatDouble (
					calibratableComponentArray[componentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						calibrationMeasureArray[componentIndex]
					),
					1,
					5,
					1.
				) + " |" + FormatUtil.FormatDouble (
					componentCalibrationValueArray[componentIndex],
					1,
					5,
					1.
				)
			);
		}

		for (int componentIndex = 0; componentIndex < calibratableComponentArray.length; ++componentIndex) {
			WengertJacobian componentWengertJacobian =
				calibratableComponentArray[componentIndex].jackDDirtyPVDManifestMeasure (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null
				);

			System.out.println (
				"\t|| PV/DF Micro Jack[" + calibratableComponentArray[componentIndex].name() + "]=> " + (
					null == componentWengertJacobian ? null : componentWengertJacobian.displayString()
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

		long startTime = System.nanoTime();

		DiscountCurveFromRatesInstruments();

		System.out.println (
			"\t|| Time Taken: " + ((int)(1.e-09 * (System.nanoTime() - startTime))) + " sec"
		);

		EnvManager.TerminateEnv();
	}
}
