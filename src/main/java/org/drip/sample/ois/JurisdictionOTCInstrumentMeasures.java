
package org.drip.sample.ois;

import java.util.Map;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.OvernightLabel;
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
 * <i>JurisdictionOTCInstrumentMeasures</i> contains the Curve Construction and Valuation Functionality of
 * 	the OTC OIS Instruments across Multiple Jurisdictions.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/ois/README.md">Index/Fund OIS Curve Reconciliation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class JurisdictionOTCInstrumentMeasures
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
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityDaysIndex = 0; maturityDaysIndex < maturityDaysArray.length; ++maturityDaysIndex) {
			depositComponentArray[maturityDaysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityDaysIndex], currency),
				OvernightLabel.Create (currency)
			);
		}

		return depositComponentArray;
	}

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			oisArray[maturityTenorIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OISFuturesFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponIndex)
		throws Exception
	{
		FixFloatComponent[] oisFuturesArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex< maturityTenorArray.length; ++maturityIndex) {
			oisFuturesArray[maturityIndex] = OTCOISFixFloat (
				spotDate.addTenor (startTenorArray[maturityIndex]),
				currency,
				maturityTenorArray[maturityIndex],
				couponIndex[maturityIndex]
			);
		}

		return oisFuturesArray;
	}

	private static final void OTCOISRun (
		final JulianDate spotDate,
		final String currency,
		final String[] otcOISMaturityTenorArray,
		final boolean displayCalibrationMetric)
		throws Exception
	{
		if (displayCalibrationMetric) {
			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t--------- DISCOUNT CURVE WITH OVERNIGHT INDEX ------------------");

			System.out.println ("\t----------------------------------------------------------------");
		}

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
			0.0004
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

		double[] oisFutureQuoteArray =
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
						oisFutureQuoteArray
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
			),
			null,
			null,
			null,
			null,
			null,
			null
		);

		if (displayCalibrationMetric) {
			System.out.println ("\t----------------------------------------------------------------");

			System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int depositIndex = 0; depositIndex < depositComponentArray.length; ++depositIndex) {
				System.out.println (
					"\t[" + depositComponentArray[depositIndex].effectiveDate() + " => " +
						depositComponentArray[depositIndex].maturityDate() + "] = " +
						FormatUtil.FormatDouble (
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
				Map<String, Double> shortEndOISComponentMap =
					shortEndOISComponentArray[shortEndOISIndex].value (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null
					);

				System.out.println (
					"\t[" + shortEndOISComponentArray[shortEndOISIndex].effectiveDate() + " => " +
						shortEndOISComponentArray[shortEndOISIndex].maturityDate() + "] = " +
						FormatUtil.FormatDouble (
							shortEndOISComponentMap.get ("CalibSwapRate"),
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							shortEndOISQuoteArray[shortEndOISIndex],
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							shortEndOISComponentMap.get ("FairPremium"),
							1,
							6,
							1.
						)
				);
			}

			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int oisFuturesIndex = 0;
				oisFuturesIndex < oisFuturesComponentArray.length;
				++oisFuturesIndex)
			{
				Map<String, Double> oisFuturesMeasureMap = oisFuturesComponentArray[oisFuturesIndex].value (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null
				);

				System.out.println (
					"\t[" + oisFuturesComponentArray[oisFuturesIndex].effectiveDate() + " => " +
						oisFuturesComponentArray[oisFuturesIndex].maturityDate() + "] = " +
						FormatUtil.FormatDouble (oisFuturesMeasureMap.get ("SwapRate"), 1, 6, 1.) + " | " +
						FormatUtil.FormatDouble (oisFutureQuoteArray[oisFuturesIndex], 1, 6, 1.) + " | " +
						FormatUtil.FormatDouble (oisFuturesMeasureMap.get ("FairPremium"), 1, 6, 1.)
				);
			}

			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int longEndOISIndex = 0;
				longEndOISIndex < longEndOISComponentArray.length;
				++longEndOISIndex)
			{
				Map<String, Double> longEndOISMeasureMap = longEndOISComponentArray[longEndOISIndex].value (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null
				);

				System.out.println (
					"\t[" + longEndOISComponentArray[longEndOISIndex].effectiveDate() + " => " +
						longEndOISComponentArray[longEndOISIndex].maturityDate() + "] = " +
						FormatUtil.FormatDouble (
							longEndOISMeasureMap.get ("CalibSwapRate"),
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							longEndOISQuoteArray[longEndOISIndex],
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							longEndOISMeasureMap.get ("FairPremium"),
							1,
							6,
							1.
						)
				);
			}

			System.out.println ("\t----------------------------------------------------------------");
		}

		System.out.print ("\t[" + currency + "] = ");

		for (int otcOISIndex = 0; otcOISIndex < otcOISMaturityTenorArray.length; ++otcOISIndex) {
			Map<String, Double> oisMeasureMap = OTCOISFixFloat (
				spotDate,
				currency,
				otcOISMaturityTenorArray[otcOISIndex],
				0.
			).value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			System.out.print (
				FormatUtil.FormatDouble (oisMeasureMap.get ("SwapRate"), 1, 4, 100.) + "% (" +
				FormatUtil.FormatDouble (oisMeasureMap.get ("FairPremium"), 1, 4, 100.) + "%) || "
			);
		}

		System.out.println();
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

		JulianDate today = DateUtil.Today();

		String[] otcOISMaturityTenor =
		{
			"1Y",
			"3Y",
			"5Y",
			"7Y",
			"10Y"
		};

		OTCOISRun (today, "AUD", otcOISMaturityTenor, true);

		System.out.println (
			"\n\t--------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t JURISDICTION       1Y      ||          3Y         ||          5Y         ||          7Y         ||         10Y         ||"
		);

		System.out.println (
			"\t--------------------------------------------------------------------------------------------------------------------------"
		);

		OTCOISRun (today, "AUD", otcOISMaturityTenor, false);

		OTCOISRun (today, "CAD", otcOISMaturityTenor, false);

		OTCOISRun (today, "EUR", otcOISMaturityTenor, false);

		OTCOISRun (today, "GBP", otcOISMaturityTenor, false);

		OTCOISRun (today, "INR", otcOISMaturityTenor, false);

		OTCOISRun (today, "JPY", otcOISMaturityTenor, false);

		OTCOISRun (today, "SGD", otcOISMaturityTenor, false);

		OTCOISRun (today, "USD", otcOISMaturityTenor, false);

		EnvManager.TerminateEnv();
	}
}
