
package org.drip.sample.rolledstate;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.historical.builder.FundingStateCustomization;
import org.drip.historical.builder.FundingStateEvaluation;
import org.drip.historical.builder.FundingStateGenerator;
import org.drip.historical.calibration.DepositInstrumentSuite;
import org.drip.historical.calibration.FixFloatInstrumentSuite;
import org.drip.historical.calibration.FundingMarkSuite;
import org.drip.historical.calibration.FuturesInstrumentSuite;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.SingleStreamComponent;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.ResponseScalingShapeControl;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.inference.LinearLatentStateCalibrator;

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
 * <i>DecayedFundingCurve</i> illustrates the build out the forward decayed Funding Curve.
 *  
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/rolledstate/README.md">Generation of T1 Decayed State</a></li>
 *  </ul>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DecayedFundingCurve
{

	private static final FundingStateEvaluation T0EOD (
		final FundingStateGenerator fundingStateGenerator,
		final FundingMarkSuite t0FundingCalibrationSuite)
		throws Exception
	{
		FundingStateCustomization fundingStateCustomization = fundingStateGenerator.customizationSettings();

		FundingStateEvaluation fundingStateEvaluation =
			new FundingStateGenerator (fundingStateCustomization).t0EOD (t0FundingCalibrationSuite);

		FixFloatInstrumentSuite fixFloatInstrumentSuite =
			fundingStateCustomization.fixFloatInstrumentSuite();

		DepositInstrumentSuite depositInstrumentSuite = fundingStateCustomization.depositInstrumentSuite();

		JulianDate t0 = t0FundingCalibrationSuite.asOfDate();

		double[] depositQuoteArray = t0FundingCalibrationSuite.depositQuoteArray();

		String depositCalibrationMetric = depositInstrumentSuite.calibrationMetric();

		SingleStreamComponent[] depositComponentArray = fundingStateEvaluation.depositComponentArray();

		ValuationParams valuationParams = new ValuationParams (t0, t0, depositInstrumentSuite.currency());

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			fundingStateEvaluation.mergedDiscountForwardCurve(),
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

		for (int depositComponentIndex = 0;
			depositComponentIndex < depositComponentArray.length;
			++depositComponentIndex)
		{
			System.out.println (
				"\t[" + depositComponentArray[depositComponentIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (
					depositComponentArray[depositComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						depositCalibrationMetric
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (depositQuoteArray[depositComponentIndex], 1, 6, 1.)
			);
		}

		System.out.println ("\t----------------------------------------------------------------");

		SingleStreamComponent[] futuresComponentArray = fundingStateEvaluation.futuresComponentArray();

		FuturesInstrumentSuite futuresInstrumentSuite = fundingStateCustomization.futuresInstrumentSuite();

		String futuresCalibrationMetric = futuresInstrumentSuite.calibrationMetric();

		double[] futuresQuoteArray = t0FundingCalibrationSuite.futuresQuoteArray();

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     EDF INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int futuresComponentIndex = 0;
			futuresComponentIndex < futuresComponentArray.length;
			++futuresComponentIndex)
		{
			System.out.println (
				"\t[" + futuresComponentArray[futuresComponentIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (
					futuresComponentArray[futuresComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						futuresCalibrationMetric
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					futuresQuoteArray[futuresComponentIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\t----------------------------------------------------------------");

		FixFloatComponent[] fixFloatComponentArray = fundingStateEvaluation.fixFloatComponentArray();

		String fixFloatCalibrationMetric = fixFloatInstrumentSuite.calibrationMetric();

		double[] fixFloatQuoteArray = t0FundingCalibrationSuite.fixFloatQuoteArray();

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int fixfloatComponentIndex = 0;
			fixfloatComponentIndex < fixFloatComponentArray.length;
			++fixfloatComponentIndex)
		{
			System.out.println (
				"\t[" + fixFloatComponentArray[fixfloatComponentIndex].maturityDate() + "] => " +
				FormatUtil.FormatDouble (
					fixFloatComponentArray[fixfloatComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						fixFloatCalibrationMetric
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					fixFloatQuoteArray[fixfloatComponentIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\t----------------------------------------------------------------");

		return fundingStateEvaluation;
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
		String depositCode = "DEPOSIT";
		String depositCalibrationMetric = "ForwardRate";
		String depositPeriodTenor = "3M";
		int[] depositMaturityDaysArray = new int[]
		{
			1,
			2,
			7,
			14,
			30,
			60
		};
		double[] t0EODDepositQuoteArray = new double[]
		{
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		};

		String futuresCode = "EDF";
		int futuresComponentCount = 8;
		String futuresCalibrationMetric = "ForwardRate";
		double[] t0EODFuturesQuoteArray = new double[]
		{
			0.0027,
			0.0032,
			0.0041,
			0.0054,
			0.0077,
			0.0104,
			0.0134,
			0.0160
		};

		String fixFloatCode = "SWAP";
		String fixFloatCalibrationMetric = "SwapRate";
		String fixFloatPeriodTenor = "6M";
		String fixFloatDayCountConvention = "Act/360";
		String[] fixFloatMaturityTenor = new String[]
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
		};
		double[] t0EODFixFloatQuoteArray = new double[]
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

		LinearLatentStateCalibrator linearLatentStateCalibrator = new LinearLatentStateCalibrator (
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
		);

		JulianDate t0 = DateUtil.Today().addTenor ("0D");

		JulianDate t1 = t0.addBusDays (1, currency);

		FundingMarkSuite t0FundingCalibrationSuite = new FundingMarkSuite (
			t0,
			t0EODDepositQuoteArray,
			t0EODFuturesQuoteArray,
			t0EODFixFloatQuoteArray
		);

		FundingStateGenerator fundingStateGenerator = new FundingStateGenerator (
			new FundingStateCustomization (
				linearLatentStateCalibrator,
				new DepositInstrumentSuite (
					depositCode,
					currency,
					depositCalibrationMetric,
					depositPeriodTenor,
					depositMaturityDaysArray
				),
				new FuturesInstrumentSuite (futuresCode, currency, futuresCalibrationMetric, futuresComponentCount),
				new FixFloatInstrumentSuite (
					fixFloatCode,
					currency,
					fixFloatCalibrationMetric,
					fixFloatPeriodTenor,
					fixFloatDayCountConvention,
					fixFloatMaturityTenor
				)
			)
		);

		FundingStateEvaluation t0EODFundingStateEvaluation = T0EOD (
			fundingStateGenerator,
			t0FundingCalibrationSuite
		);

		FundingStateEvaluation t1SODFundingStateEvaluation = fundingStateGenerator.t1SOD (
			t1,
			t0EODFundingStateEvaluation.mergedDiscountForwardCurve()
		);

		FundingMarkSuite t1SODFundingCalibrationSuite = t1SODFundingStateEvaluation.fundingMarkSuite();

		double[] t1SODDepositQuoteArray = t1SODFundingCalibrationSuite.depositQuoteArray();

		System.out.println ("\t----------------------------------------------------------------");

		System.out.println ("\t     ROLLED OVER DEPOSIT INSTRUMENTS CALIBRATION");

		System.out.println ("\t----------------------------------------------------------------");

		for (int depositComponentIndex = 0;
			depositComponentIndex < t1SODDepositQuoteArray.length;
			++depositComponentIndex)
		{
			System.out.println (
				"\t[" + depositComponentIndex + "] = " +
				FormatUtil.FormatDouble (t1SODDepositQuoteArray[depositComponentIndex],1 ,6, 1.) + " | " +
				FormatUtil.FormatDouble (t0EODDepositQuoteArray[depositComponentIndex], 1, 6, 1.)
			);
		}

		System.out.println ("\t----------------------------------------------------------------\n");

		double[] t1SODFuturesQuoteArray = t1SODFundingCalibrationSuite.futuresQuoteArray();

		System.out.println ("\t----------------------------------------------------------------");

		System.out.println ("\t     ROLLED OVER EDF INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int futuresComponentIndex = 0;
			futuresComponentIndex < t1SODFuturesQuoteArray.length;
			++futuresComponentIndex)
		{
			System.out.println (
				"\t[" + futuresComponentIndex + "] = " +
				FormatUtil.FormatDouble (t1SODFuturesQuoteArray[futuresComponentIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (t0EODFuturesQuoteArray[futuresComponentIndex], 1, 6, 1.)
			);
		}

		System.out.println ("\t----------------------------------------------------------------");

		double[] t1SODFixFloatQuoteArray = t1SODFundingCalibrationSuite.fixFloatQuoteArray();

		for (int fixfloatComponentIndex = 0;
			fixfloatComponentIndex < t1SODFixFloatQuoteArray.length;
			++fixfloatComponentIndex)
		{
			System.out.println (
				"\t[" + fixfloatComponentIndex + "] => " +
				FormatUtil.FormatDouble (t1SODFixFloatQuoteArray[fixfloatComponentIndex], 1, 6, 1) + " | " +
				FormatUtil.FormatDouble (t0EODFixFloatQuoteArray[fixfloatComponentIndex], 1, 6, 1)
			);
		}

		System.out.println ("\t----------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
