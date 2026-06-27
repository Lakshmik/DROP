
package org.drip.sample.capfloor;

import java.util.*;

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
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.fra.*;
import org.drip.product.params.LastTradingDateSetting;
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
 * <i>FRAStdCapMonteCarlo</i> demonstrates the steps associated with a LMM-Based Monte-Carlo pricing of a FRA
 * 	Cap. The References are:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics
 * 				<i>Mathematical Finance</i> <b>7 (2)</b> 127-155
 *  	</li>
 *  	<li>
 * 			Goldys, B., M. Musiela, and D. Sondermann (1994): <i>Log-normality of Rates and Term Structure
 * 				Models</i> <b>The University of New South Wales</b>
 *  	</li>
 *  	<li>
 * 			Musiela, M. (1994): <i>Nominal Annual Rates and Log-normal Volatility Structure</i> <b>The
 * 				University of New South Wales</b>
 *  	</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/capfloor/README.md">FRA Standard Cap Floor Valuation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FRAStdCapMonteCarlo
{

	/*
	 * Construct the Array of Deposit Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] dayArray)
		throws Exception
	{
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[dayArray.length];

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

		for (int  dayIndex = 0; dayIndex < dayArray.length; ++dayIndex) {
			depositComponentArray[dayIndex] = new SingleStreamComponent (
				"DEPOSIT_" + dayArray[dayIndex],
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.EdgePair (
							effectiveDate,
							effectiveDate.addBusDays (dayArray[dayIndex], currency)
						),
						compositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			depositComponentArray[dayIndex].setPrimaryCode (dayArray[dayIndex] + "D");
		}

		return depositComponentArray;
	}

	/*
	 * Construct the Swap Instrument from the given set of parameters
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent SwapInstrumentFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final double fixedCoupon,
		final String maturityTenor)
		throws Exception
	{
		FixFloatComponent irsFixFloatComponent = new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (effectiveDate, "3M", maturityTenor, null),
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
					CompositePeriodBuilder.RegularEdgeDates (effectiveDate, "3M", maturityTenor, null),
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

		irsFixFloatComponent.setPrimaryCode ("IRS." + maturityTenor + "." + currency);

		return irsFixFloatComponent;
	}

	/*
	 * Construct the Array of Swap Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsFixFloatComponentArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			irsFixFloatComponentArray[maturityTenorIndex] = SwapInstrumentFromMaturityTenor (
				effectiveDate,
				currency,
				0.,
				maturityTenorArray[maturityTenorIndex]
			);
		}

		return irsFixFloatComponentArray;
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
		/*
		 * Construct the Array of Deposit Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[] {
				1,
				2,
				7,
				14,
				30,
				60
			}
		);

		double[] depositQuoteArray = new double[] {
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		};

		/*
		 * Construct the Deposit Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec depositStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"DEPOSIT",
			depositComponentArray,
			"ForwardRate",
			depositQuoteArray
		);

		/*
		 * Construct the Array of EDF Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] edfComponentArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			spotDate,
			8,
			currency
		);

		double[] edfQuoteArray = new double[] {
			0.0027,
			0.0032,
			0.0041,
			0.0054,
			0.0077,
			0.0104,
			0.0134,
			0.0160
		};

		/*
		 * Construct the EDF Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec edfStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"EDF",
			edfComponentArray,
			"ForwardRate",
			edfQuoteArray
		);

		/*
		 * Construct the Array of Swap Instruments and their Quotes from the given set of parameters
		 */

		FixFloatComponent[] fixFloatComponentArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
			new String[] {
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

		double[] fixFloatQuoteArray = new double[] {
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

		/*
		 * Construct the Swap Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec fixFloatComponentStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"SWAP",
			fixFloatComponentArray,
			"SwapRate",
			fixFloatQuoteArray
		);

		/*
		 * Set up the Linear Curve Calibrator using the following parameters:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Natural Boundary Setting
		 */

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

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		/*
		 * Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
		 *  of Deposit, Futures, and Swap Stretches.
		 */

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			linearLatentStateCalibrator,
			new LatentStateStretchSpec[] {depositStretch, edfStretch, fixFloatComponentStretch},
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

		/*
		 * Cross-Comparison of the Deposit Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

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
							"Rate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						depositQuoteArray[depositComponentIndex],
						1,
						6,
						1.
					)
			);
		}

		/*
		 * Cross-Comparison of the EDF Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     EDF INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int edfComponentIndex = 0; edfComponentIndex < edfComponentArray.length; ++edfComponentIndex) {
			System.out.println (
				"\t[" + edfComponentArray[edfComponentIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						edfComponentArray[edfComponentIndex].measureValue (
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
						edfQuoteArray[edfComponentIndex],
						1,
						6,
						1.
					)
			);
		}

		/*
		 * Cross-Comparison of the Swap Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int fixFloatComponentIndex = 0;
			fixFloatComponentIndex < fixFloatComponentArray.length;
			++fixFloatComponentIndex)
		{
			System.out.println (
				"\t[" + fixFloatComponentArray[fixFloatComponentIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						fixFloatComponentArray[fixFloatComponentIndex].measureValue (
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
						fixFloatQuoteArray[fixFloatComponentIndex],
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						fixFloatComponentArray[fixFloatComponentIndex].measureValue (
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
		double[] dateArray = new double[forwardTenorCount + 1];
		double[] liborArray = new double[forwardTenorCount + 1];
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[forwardTenorCount];

		JulianDate forwardDate = viewDate.subtractTenor (forwardLabel.tenor());

		for (int forwardTenorIndex = 0; forwardTenorIndex <= forwardTenorCount; ++forwardTenorIndex) {
			if (forwardTenorCount != forwardTenorIndex) {
				segmentCustomBuilderControlArray[forwardTenorIndex] = segmentCustomBuilderControl;
			}

			dateArray[forwardTenorIndex] = forwardDate.julian();

			liborArray[forwardTenorIndex] = discountCurve.libor (forwardDate, forwardLabel.tenor());

			forwardDate = forwardDate.addTenor (forwardLabel.tenor());
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
		JulianDate startDatePlus2Y = startDate.addYears (2);

		JulianDate startDatePlus4Y = startDate.addYears (4);

		JulianDate startDatePlus6Y = startDate.addYears (6);

		JulianDate startDatePlus8Y = startDate.addYears (8);

		JulianDate startDatePlus10Y = startDate.addYears (10);

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			startDate,
			currency,
			new double[] {
				startDate.julian(),
				startDatePlus2Y.julian(),
				startDatePlus4Y.julian(),
				startDatePlus6Y.julian(),
				startDatePlus8Y.julian(),
				startDatePlus10Y.julian()
			},
			new double[] {
				startDate.julian(),
				startDatePlus2Y.julian(),
				startDatePlus4Y.julian(),
				startDatePlus6Y.julian(),
				startDatePlus8Y.julian(),
				startDatePlus10Y.julian()
			},
			new double[][] {
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

		for (int univariateSequenceGeneratorIndex = 0;
			univariateSequenceGeneratorIndex < univariateSequenceGeneratorArray.length;
			++univariateSequenceGeneratorIndex)
		{
			univariateSequenceGeneratorArray[univariateSequenceGeneratorIndex] = new BoxMullerGaussian (
				0.,
				1.
			);
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

		int runCount = 100;
		int factorCount = 2;
		double strike = 0.02;
		String currency = "USD";
		String viewTenor = "6M";
		int forwardTenorCount = 30;
		String forwardTenor = "3M";
		String maturityTenor = "5Y";
		String simulationTenor = "6M";
		double flatVolatility1 = 0.35;
		double flatVolatility2 = 0.42;
		double flatVolatility3 = 0.27;
		String manifestMeasure = "ParForward";

		double[][] correlationMatrix = new double[][] {
			{1.0, 0.1, 0.2},
			{0.1, 1.0, 0.2},
			{0.2, 0.1, 1.0}
		};

		SegmentCustomBuilderControl segmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (1.)),
			null
		);

		JulianDate spotDate = DateUtil.Today();

		JulianDate viewDate = spotDate.addTenor (viewTenor);

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		JulianDate simulationEndDate = spotDate.addTenor (simulationTenor);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, forwardTenor);

		MergedDiscountForwardCurve discountCurve = OTCInstrumentCurve (spotDate, currency);

		ForwardCurve[] liborForwardCurveArray = LognormalLIBORCurveEvolver.Create (
			fundingLabel,
			forwardLabel,
			forwardTenorCount,
			segmentCustomBuilderControl
		).simulateTerminalLatentState (
			spotDate.julian(),
			simulationEndDate.julian(),
			1,
			viewDate.julian(),
			BGMCurveUpdate.Create (
				fundingLabel,
				forwardLabel,
				spotDate.julian(),
				spotDate.julian(),
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
					spotDate.julian(),
					forwardLabel,
					new MarketSurface[] {
						FlatVolatilitySurface (spotDate, currency, flatVolatility1),
						FlatVolatilitySurface (spotDate, currency, flatVolatility2),
						FlatVolatilitySurface (spotDate, currency, flatVolatility3)
					},
					correlationMatrix,
					factorCount
				)
			),
			runCount
		);

		List<FRAStandardCapFloorlet> fraStandardCapletList = new FRAStandardCapFloor (
			"FRA_CAP",
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						viewDate.julian(),
						forwardTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (4, forwardTenor, currency, null, 1., null, null, null, null),
					new ComposableFloatingUnitSetting (
						forwardTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
						null,
						forwardLabel,
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			manifestMeasure,
			true,
			strike,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			null,
			new BlackScholesAlgorithm()
		).capFloorlets();

		System.out.println ("\n\t||--------------------------------------------------||");

		System.out.println ("\t||           DATES           => CAP LEEFT | FLR LFT ||");

		System.out.println ("\t||--------------------------------------------------||");

		ValuationParams finalValuationParams = new ValuationParams (
			simulationEndDate,
			simulationEndDate,
			currency
		);

		double capLift = 0.;
		double floorLift = 0.;

		for (int runIndex = 0; runIndex < runCount; ++runIndex) {
			CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.DiscountForward (
				discountCurve,
				liborForwardCurveArray[runIndex]
			);

			for (FRAStandardCapFloorlet fraStandardCaplet : fraStandardCapletList) {
				FRAStandardComponent fra = fraStandardCaplet.fra();

				Map<String, Double> scenatioFRAOutputMap = fra.value (
					finalValuationParams,
					null,
					curveSurfaceQuoteContainer,
					null
				);

				double scenarioCapLift = scenatioFRAOutputMap.get ("CapLift");

				double scenarioFloorLift = scenatioFRAOutputMap.get ("FloorLift");

				capLift += scenarioCapLift;
				floorLift += scenarioFloorLift;

				System.out.println (
					"\t|| [" + fra.effectiveDate() + " - " + fra.maturityDate() + "] => " +
					FormatUtil.FormatDouble (scenarioCapLift, 1, 5, 1.) + " | " +
					FormatUtil.FormatDouble (scenarioFloorLift, 1, 5, 1.) + " ||"
				);
			}
		}

		capLift = capLift / runCount;
		floorLift = floorLift / runCount;

		double terminalDF = discountCurve.df (simulationEndDate);

		System.out.println ("\t||--------------------------------------------------||");

		System.out.println ("\n\n\t\t||-------------------------||");

		System.out.println ("\t\t|| Cap Lift   : " + FormatUtil.FormatDouble (capLift, 1, 5, 1.) + " ||");

		System.out.println (
			"\t\t|| Floor Lift : " + FormatUtil.FormatDouble (floorLift, 1, 5, 1.) + " ||"
		);

		System.out.println (
			"\t\t|| Cap PV     : " + FormatUtil.FormatDouble (capLift * terminalDF, 1, 5, 1.) + " ||"
		);

		System.out.println (
			"\t\t|| Floor PV   : " + FormatUtil.FormatDouble (floorLift * terminalDF, 1, 5, 1.) + " ||"
		);

		System.out.println ("\t\t||-------------------------||");

		EnvManager.TerminateEnv();
	}
}
