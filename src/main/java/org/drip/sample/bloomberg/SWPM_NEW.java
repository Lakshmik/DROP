
package org.drip.sample.bloomberg;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.sample.forward.IBORCurve;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
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
 * <i>SWPM_NEW</i> contains the sample demonstrating the replication of Bloomberg's Latest SWPM
 * 	Functionality.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/bloomberg/README.md">Bloomberg CDSO CDSW SWPM YAS</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class SWPM_NEW
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

		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		for (int maturityDaysIndex = 0; maturityDaysIndex < maturityDaysArray.length; ++maturityDaysIndex) {
			depositComponentArray[maturityDaysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityDaysIndex], currency),
				overnightLabel
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

	private static final MergedDiscountForwardCurve OvernightCurve (
		final JulianDate spotDate,
		final String currency,
		final String header)
		throws Exception
	{
		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     " + header);

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

		ValuationParams valuationParams = new ValuationParams (
			spotDate,
			spotDate,
			currency
		);

		MergedDiscountForwardCurve overnightCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
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
					depositComponentArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_SHORT_END",
					shortEndOISComponentArray,
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_FUTURE",
					oisFuturesComponentArray,
					"SwapRate",
					oisFuturesQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"OIS_LONG_END",
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

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Create (overnightCurve, null, null, null, null, null, null);

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositComponentArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositComponentArray[depositIndex].effectiveDate() + " => " +
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

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int shortEndOISIndex = 0;
			shortEndOISIndex < shortEndOISComponentArray.length;
			++shortEndOISIndex)
		{
			System.out.println (
				"\t|| [" + shortEndOISComponentArray[shortEndOISIndex].effectiveDate() + " => " +
					shortEndOISComponentArray[shortEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						shortEndOISComponentArray[shortEndOISIndex].measureValue (
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
						shortEndOISQuoteArray[shortEndOISIndex],
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						shortEndOISComponentArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
				);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesComponentArray.length; ++oisFuturesIndex) {
			System.out.println (
				"\t|| [" + oisFuturesComponentArray[oisFuturesIndex].effectiveDate() + " => " +
					oisFuturesComponentArray[oisFuturesIndex].maturityDate() + "] = " +
						FormatUtil.FormatDouble (
							oisFuturesComponentArray[oisFuturesIndex].measureValue (
								valuationParams,
								null,
								curveSurfaceQuoteContainer,
								null,
								"SwapRate"
							),
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							oisFuturesQuoteArray[oisFuturesIndex],
							1,
							6,
							1.
						) + " | " + FormatUtil.FormatDouble (
							oisFuturesComponentArray[oisFuturesIndex].measureValue (
								valuationParams,
								null,
								curveSurfaceQuoteContainer,
								null,
								"FairPremium"
							),
							1,
							6,
							1.
						)
				);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISComponentArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t|| [" + longEndOISComponentArray[longEndOISIndex].effectiveDate() + " => " +
					longEndOISComponentArray[longEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						longEndOISComponentArray[longEndOISIndex].measureValue (
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
						longEndOISQuoteArray[longEndOISIndex],
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						longEndOISComponentArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
				);
		}

		return overnightCurve;
	}

	private static final ForwardCurve MakeForwardCurve (
		final JulianDate valueDate,
		final MergedDiscountForwardCurve overnightCurve,
		final String forwardTenor)
		throws Exception
	{
		String currency = overnightCurve.currency();

		return IBORCurve.CustomIBORBuilderSample (
			overnightCurve,
			null,
			ForwardLabel.Create (currency, forwardTenor),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			),
			new String[]
			{
				"1D",
				"1W",
				"2W",
				"3W",
				"1M",
				"2M",
			},
			new double[]
			{
				0.003565,	// 1D
				0.003858,	// 1W
				0.003840,	// 2W
				0.003922,	// 3W
				0.003869,	// 1M
				0.003698,	// 2M
			},
			"ForwardRate",
			new String[]
			{
				 "0D",
				 "1M",
				 "2M",
				 "3M",
				 "4M",
				 "5M",
				 "6M",
				 "7M",
				 "8M",
				 "9M",
				"10M",
				"11M",
				"12M",
				"13M",
				"14M",
				"15M",
				"16M",
				"17M",
				"18M"
			},
			new double[]
			{
				0.003120,	//  0D
				0.002930,	//  1M
				0.002720,	//  2M
				0.002600,	//  3M
				0.002560,	//  4M
				0.002520,	//  5M
				0.002480,	//  6M
				0.002540,	//  7M
				0.002610,	//  8M
				0.002670,	//  9M
				0.002790,	// 10M
				0.002910,	// 11M
				0.003030,	// 12M
				0.003180,	// 13M
				0.003350,	// 14M
				0.003520,	// 15M
				0.003710,	// 16M
				0.003890,	// 17M
				0.004090	// 18M
			},
			"ParForwardRate",
			new String[]
			{
				 "3Y",
				 "4Y",
				 "5Y",
				 "6Y",
				 "7Y",
				 "8Y",
				 "9Y",
				"10Y",
				"12Y",
				"15Y",
				"20Y",
				"25Y",
				"30Y",
				"35Y",
				"40Y",
				"50Y",
				"60Y"
			},
			new double[]
			{
				0.004240,	//  3Y
				0.005760,	//  4Y			
				0.007620,	//  5Y
				0.009540,	//  6Y
				0.011350,	//  7Y
				0.013030,	//  8Y
				0.014520,	//  9Y
				0.015840,	// 10Y
				0.018090,	// 12Y
				0.020370,	// 15Y
				0.021870,	// 20Y
				0.022340,	// 25Y
				0.022560,	// 30Y
				0.022950,	// 35Y
				0.023480,	// 40Y
				0.024210,	// 50Y
				0.024630	// 60Y
			},
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			null,
			null,
			"DerivedParBasisSpread",
			"---- " + currency + "-LIBOR-" + forwardTenor + " VANILLA CUBIC POLYNOMIAL FORWARD CURVE ---",
			true
		);
	}

	private static final FixFloatComponent FixFloatSwap (
		final JulianDate effectiveDate,
		final ForwardLabel forwardLabel,
		final String fixedTenor,
		final String maturityTenor,
		final double coupon)
		throws Exception
	{
		String currency = forwardLabel.currency();

		String forwardTenor = forwardLabel.tenor();

		FixFloatComponent fixFloatComponent = new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.BackwardEdgeDates (
						effectiveDate,
						effectiveDate.addTenor (maturityTenor),
						fixedTenor,
						null,
						CompositePeriodBuilder.SHORT_STUB
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
					new UnitCouponAccrualSetting (
						2,
						"Act/360",
						false,
						"Act/360",
						false,
						currency,
						false,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						fixedTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						coupon,
						0.,
						currency
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						forwardTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / Integer.parseInt (forwardTenor.split ("M")[0]),
						forwardTenor,
						currency,
						null,
						-1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						forwardTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						forwardLabel,
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			null
		);

		fixFloatComponent.setPrimaryCode ("FixFloat: " + maturityTenor);

		return fixFloatComponent;
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
		EnvManager.InitEnv ("", true);

		String currency = "USD";
		double coupon = 0.026825;
		String fixedTenor = "6M";
		String forwardTenor = "3M";
		String maturityTenor = "10Y";

		JulianDate spotDate = DateUtil.CreateFromYMD (2015, DateUtil.MAY, 15);

		JulianDate effectiveDate = DateUtil.CreateFromYMD (2017, DateUtil.SEPTEMBER, 11);

		MergedDiscountForwardCurve overnightCurve = OvernightCurve (
			spotDate,
			currency,
			"OVERNIGHT INDEX RUN RECONCILIATION"
		);

		ForwardCurve forwardCurve3M = MakeForwardCurve (spotDate, overnightCurve, forwardTenor);

		for (Map.Entry<String, Double> mapEntry : FixFloatSwap (
				effectiveDate,
				forwardCurve3M.index(),
				fixedTenor,
				maturityTenor,
				coupon
			).value (
				new ValuationParams (spotDate, spotDate, currency),
				null,
				MarketParamsBuilder.Create (
					overnightCurve,
					forwardCurve3M,
					null,
					null,
					null,
					null,
					null,
					null
				),
				null
			).entrySet()
		)
		{
			System.out.println (
				"\t|| " + mapEntry.getKey() + " => " +
					FormatUtil.FormatDouble (mapEntry.getValue(), 1, 8, 1.) + " |"
			);
		}

		EnvManager.TerminateEnv();
	}
}
