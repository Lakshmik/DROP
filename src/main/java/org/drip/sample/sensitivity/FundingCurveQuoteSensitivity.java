
package org.drip.sample.sensitivity;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.numerical.differentiation.WengertJacobian;
import org.drip.param.creator.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
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
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * <i>FundingCurveQuoteSensitivity</i> demonstrates the calculation of the Funding curve sensitivity to the
 * 	calibration instrument quotes. It does the following:
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
 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
 * 	- Display of the Swap Instrument Discount Factor Quote Jacobian Sensitivities.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/sensitivity/README.md">Forward Funding OIS Curve Sensitivity</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FundingCurveQuoteSensitivity
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

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				overnightLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int irsIndex = 0; irsIndex < maturityTenorArray.length; ++irsIndex) {
			irsArray[irsIndex] = OTCIRS (
				spotDate,
				currency,
				maturityTenorArray[irsIndex],
				0.
			);
		}

		return irsArray;
	}

	private static final void TenorJack (
		final JulianDate startDate,
		final String tenor,
		final String currency,
		final String manifestMeasure,
		final MergedDiscountForwardCurve discountCurve)
		throws Exception
	{
		CalibratableComponent irsBespoke = OTCIRS (startDate, currency, tenor, 0.);

		System.out.println (
			"\t|| " + tenor + " => " + discountCurve.jackDDFDManifestMeasure (
				irsBespoke.maturityDate(),
				manifestMeasure
			).displayString()
		);
	}

	private static final void Forward6MRateJack (
		final JulianDate startDate,
		final String startTenor,
		final String manifestMeasure,
		final MergedDiscountForwardCurve discountCurve)
	{
		JulianDate beginDate = startDate.addTenor (startTenor);

		System.out.println (
			"\t|| [" + beginDate + " | 6M] => " + discountCurve.jackDForwardDManifestMeasure (
				beginDate,
				"6M",
				manifestMeasure,
				0.5
			).displayString()
		);
	}

	/*
	 * This sample demonstrates the calculation of the discount curve sensitivity to the calibration
	 * 	instrument quotes. It does the following:
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
	 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
	 * 	- Display of the Swap Instrument Discount Factor Quote Jacobian Sensitivities.
	 */

	private static final void DiscountCurveQuoteSensitivitySample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
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

		double[] depositQuoteArray = new double[]
		{
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		}; // Cash Rate

		LatentStateStretchSpec depositStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"DEPOSIT",
			depositArray,
			"ForwardRate",
			depositQuoteArray
		);

		SingleStreamComponent[] futuresArray =
			SingleStreamComponentBuilder.ForwardRateFuturesPack (spotDate, 8, currency);

		double[] futuresQuoteArray = new double[]
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

		LatentStateStretchSpec futuresStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"EDF",
			futuresArray,
			"ForwardRate",
			futuresQuoteArray
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

		LatentStateStretchSpec swapStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"SWAP",
			irsArray,
			"SwapRate",
			swapQuoteArray
		);

		LinearLatentStateCalibrator linearLatentStateCalibrator = new LinearLatentStateCalibrator (
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			),
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			depositStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			)
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			futuresStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (false, 1, null)
			)
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			swapStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			)
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			linearLatentStateCalibrator,
			new LatentStateStretchSpec[]
			{
				depositStretch,
				futuresStretch,
				swapStretch
			},
			valuationParams,
			null,
			null,
			null,
			1.
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] => " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
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
				) + " ||"
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int futuresIndex = 0; futuresIndex < futuresArray.length; ++futuresIndex) {
			System.out.println (
				"\t|| [" + futuresArray[futuresIndex].maturityDate() + "] => " + FormatUtil.FormatDouble (
					futuresArray[futuresIndex].measureValue (
						valuationParams,
						null,
						MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					futuresQuoteArray[futuresIndex],
					1,
					6,
					1.
				) + " ||"
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
						MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
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
				) + " ||"
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| " + depositArray[depositIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						depositArray[depositIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     FUTURE MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int futuresIndex = 0; futuresIndex < futuresArray.length; ++futuresIndex) {
			System.out.println (
				"\t|| " + futuresArray[futuresIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						futuresArray[futuresIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SWAP MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int irsIndex = 0; irsIndex < irsArray.length; ++irsIndex) {
			System.out.println (
				"\t|| " + irsArray[irsIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						irsArray[irsIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     COMPONENT-BY-COMPONENT QUOTE JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		WengertJacobian wengertJacobian = discountCurve.compJackDPVDManifestMeasure (spotDate);

		if (null != wengertJacobian) {
			System.out.println (wengertJacobian.displayString());
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     BESPOKE 35Y SWAP QUOTE JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println (
			"\t|| " + OTCIRS (
				spotDate,
				currency,
				"35Y",
				0.
			).jackDDirtyPVDManifestMeasure (
				valuationParams,
				null,
				MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null, null),
				null
			).displayString()
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     BESPOKE SWAP MATURITY QUOTE JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		TenorJack (spotDate, "30Y", currency, "PV", discountCurve);

		TenorJack (spotDate, "32Y", currency, "PV", discountCurve);

		TenorJack (spotDate, "34Y", currency, "PV", discountCurve);

		TenorJack (spotDate, "36Y", currency, "PV", discountCurve);

		TenorJack (spotDate, "38Y", currency, "PV", discountCurve);

		TenorJack (spotDate, "40Y", currency, "PV", discountCurve);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DISCOUNT CURVE IMPLIED 6M FORWARD RATE QUOTE JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		Forward6MRateJack (spotDate, "1D", "PV", discountCurve);

		Forward6MRateJack (spotDate, "3M", "PV", discountCurve);

		Forward6MRateJack (spotDate, "6M", "PV", discountCurve);

		Forward6MRateJack (spotDate, "1Y", "PV", discountCurve);

		Forward6MRateJack (spotDate, "2Y", "PV", discountCurve);

		Forward6MRateJack (spotDate, "5Y", "PV", discountCurve);

		System.out.println ("\t||----------------------------------------------------------------");
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

		DiscountCurveQuoteSensitivitySample (DateUtil.Today(), currency);

		EnvManager.TerminateEnv();
	}
}
