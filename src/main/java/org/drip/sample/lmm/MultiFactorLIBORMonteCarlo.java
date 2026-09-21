
package org.drip.sample.lmm;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.dynamics.lmm.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.rates.*;
import org.drip.sequence.random.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.grid.OverlappingStretchSpan;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.*;
import org.drip.state.curve.BasisSplineForwardRate;
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
 * <i>MultiFactorLIBORMonteCarlo</i> demonstrates the Monte-Carlo Evolution Sequence of the LIBOR Forward
 * 	Curve. The References are:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): Log-normality of Rates and Term Structure
 *  			Models, The University of New South Wales.
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): Nominal Annual Rates and Log-normal Volatility Structure, The University of
 *  			New South Wales.
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics,
 * 				Mathematical Finance 7 (2), 127-155.
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/lmm/README.md">LMM Multi-Factor Monte Carlo</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MultiFactorLIBORMonteCarlo
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
			ForwardLabel.Create (currency, "3M"),
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
	 * This sample demonstrates discount curve calibration and input instrument calibration quote recovery.
	 * 	It shows the following:
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
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final MergedDiscountForwardCurve OTCInstrumentCurve (
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

		double[] depositQuoteArray =
		{
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		};

		SingleStreamComponent[] futuresArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			spotDate,
			8,
			currency
		);

		double[] futuresQuoteArray =
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
					"DEPOSIT",
					depositArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"EDF",
					futuresArray,
					"ForwardRate",
					futuresQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SWAP",
					irsArray,
					"SwapRate",
					swapQuoteArray
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
				) + " | " + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     EDF INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int futuresIndex = 0; futuresIndex < futuresArray.length; ++futuresIndex) {
			System.out.println (
				"\t|| [" + futuresArray[futuresIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
					futuresArray[futuresIndex].measureValue (
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
					futuresQuoteArray[futuresIndex],
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
				) + " | " + FormatUtil.FormatDouble (
					irsArray[irsIndex].measureValue (
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

		return discountCurve;
	}

	private static final ForwardCurve LIBORSpan (
		final MergedDiscountForwardCurve discountCurve,
		final ForwardLabel forwardLabel,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final JulianDate viewDate,
		final int forwardTenorCount)
		throws Exception
	{
		int[] dateArray = new int[forwardTenorCount + 1];
		double[] liborArray = new double[forwardTenorCount + 1];
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[forwardTenorCount];

		String forwardTenor = forwardLabel.tenor();

		JulianDate forwardDate = viewDate.subtractTenor (forwardTenor);

		for (int forwardTenorIndex = 0; forwardTenorIndex <= forwardTenorCount; ++forwardTenorIndex) {
			if (forwardTenorCount != forwardTenorIndex) {
				segmentCustomBuilderControlArray[forwardTenorIndex] = segmentCustomBuilderControl;
			}

			liborArray[forwardTenorIndex] = discountCurve.libor (forwardDate, forwardTenor);

			dateArray[forwardTenorIndex] = forwardDate.julian();

			forwardDate = forwardDate.addTenor (forwardTenor);
		}

		return new BasisSplineForwardRate (
			forwardLabel,
			new OverlappingStretchSpan (
				MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
					"SPOT_QM_LIBOR",
					dateArray,
					liborArray,
					segmentCustomBuilderControlArray,
					null,
					BoundarySettings.NaturalStandard(),
					MultiSegmentSequence.CALIBRATE
				)
			)
		);
	}

	private static final MarketSurface FlatVolatilitySurface (
		final JulianDate startDate,
		final String currency,
		final double flatVolatility)
		throws Exception
	{
		double[] nodeDateArray =
		{
			startDate.julian(),
			startDate.addYears (2).julian(),
			startDate.addYears (4).julian(),
			startDate.addYears (6).julian(),
			startDate.addYears (8).julian(),
			startDate.addYears (10).julian()
		};

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			startDate,
			currency,
			nodeDateArray,
			nodeDateArray,
			new double[][]
			{
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
			},
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			)
		);
	}

	private static final LognormalLIBORVolatility LLVInstance (
		final int spotDate,
		final ForwardLabel forwardLabel,
		final MarketSurface[] marketSurfaceArray,
		final double[][] correlationMatrix,
		final int factorCount)
		throws Exception
	{
		UnivariateSequenceGenerator[] univariateSequenceGeneratorArray =
			new UnivariateSequenceGenerator[marketSurfaceArray.length];

		for (int sequenceIndex = 0; sequenceIndex < univariateSequenceGeneratorArray.length; ++sequenceIndex)
		{
			univariateSequenceGeneratorArray[sequenceIndex] = new BoxMullerGaussian (0., 1.);
		}

		return new LognormalLIBORVolatility (
			spotDate,
			forwardLabel,
			marketSurfaceArray,
			new PrincipalFactorSequenceGenerator (
				univariateSequenceGeneratorArray,
				correlationMatrix,
				factorCount
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

		int factorCount = 2;
		String currency = "USD";
		String viewTenor = "1Y";
		int forwardTenorCount = 5;
		String forwardTenor = "3M";
		String simulationTenor = "1M";
		double flatVolatility1 = 0.35;
		double flatVolatility2 = 0.42;
		double flatVolatility3 = 0.27;
		double[][] correlationMatrix =
		{
			{
				1.0,
				0.1,
				0.2
			},
			{
				0.1,
				1.0,
				0.2
			},
			{
				0.2,
				0.1,
				1.0
			}
		};

		SegmentCustomBuilderControl segmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (1.)),
			null
		);

		JulianDate spotDate = DateUtil.Today();

		int spotDateJulian = spotDate.julian();

		JulianDate viewDate = spotDate.addTenor (viewTenor);

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, forwardTenor);

		MergedDiscountForwardCurve discountCurve = OTCInstrumentCurve (spotDate, currency);

		double[][] tenorLIBORGrid = LognormalLIBORCurveEvolver.Create (
			fundingLabel,
			forwardLabel,
			forwardTenorCount,
			segmentCustomBuilderControl
		).simulatePrincipalMetric (
			spotDateJulian,
			spotDate.addTenor (simulationTenor).julian(),
			1,
			viewDate.julian(),
			BGMCurveUpdate.Create (
				fundingLabel,
				forwardLabel,
				spotDateJulian,
				spotDateJulian,
				LIBORSpan (
					discountCurve,
					forwardLabel,
					segmentCustomBuilderControl,
					viewDate,
					forwardTenorCount
				),
				null,
				discountCurve,
				null,
				null,
				null,
				null,
				null,
				LLVInstance (
					spotDateJulian,
					forwardLabel,
					new MarketSurface[]
					{
						FlatVolatilitySurface (spotDate, currency, flatVolatility1),
						FlatVolatilitySurface (spotDate, currency, flatVolatility2),
						FlatVolatilitySurface (spotDate, currency, flatVolatility3)
					},
					correlationMatrix,
					factorCount
				)
			),
			2
		);

		String boundary = "\t|---";
		String tenorDump = "\t|              |";
		JulianDate[] forwardDateArray = new JulianDate[forwardTenorCount + 1];

		for (int tenorDateIndex = 0; tenorDateIndex <= forwardTenorCount; ++tenorDateIndex) {
			forwardDateArray[tenorDateIndex] = 0 == tenorDateIndex ?
				viewDate.addTenor (forwardTenor) :
				forwardDateArray[tenorDateIndex - 1].addTenor (forwardTenor);

			boundary += "-----------------";
			tenorDump += "  " + forwardDateArray[tenorDateIndex] + "  |";
		}

		System.out.println ("\n\n" + boundary + "\n" + tenorDump + "\n" + boundary);

		for (int simulationDateIndex = 0; simulationDateIndex < tenorLIBORGrid.length; ++simulationDateIndex)
		{
			double[] curveNodeArray = tenorLIBORGrid[simulationDateIndex];

			String curveNodeDump = "\t|  " + spotDate.addDays (simulationDateIndex + 1) + " =>";

			for (int nodeIndex = 0; nodeIndex < tenorLIBORGrid[nodeIndex].length; ++nodeIndex) {
				curveNodeDump +=
					"   " + FormatUtil.FormatDouble (curveNodeArray[nodeIndex], 1, 3, 100.) +  "%    |";
			}

			System.out.println (curveNodeDump);
		}

		System.out.println (boundary);

		EnvManager.TerminateEnv();
	}
}
