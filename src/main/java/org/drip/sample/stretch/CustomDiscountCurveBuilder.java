	
package org.drip.sample.stretch;

/*
 * Java Imports
 */

import java.util.*;

import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.spline.stretch.MultiSegmentSequenceModifier;

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
 * <i>CustomDiscountCurveBuilder</i> contains samples that demo how to build a discount curve from purely the
 * 	cash flows. It provides for elaborate curve builder control, both at the segment level and at the Stretch
 *  level. In particular, it shows the following:
 * 	- Construct a discount curve from the discount factors available purely from the cash and the euro-dollar
 *  	instruments.
 * 	- Construct a discount curve from the cash flows available from the swap instruments.
 * 
 * In addition, the sample demonstrates the following ways of controlling curve construction:
 * 	- Control over the type of segment basis spline
 * 	- Control over the polynomial basis spline order, Ck, and tension parameters
 * 	- Provision of custom shape controllers (in this case rational shape controller)
 * 	- Calculation of segment monotonicity and convexity
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/SplineBuilderLibrary.md">Spline Builder Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/stretch/README.md">Knot Insertion Curvature Roughness Penalty</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomDiscountCurveBuilder
{

	private static final SegmentCustomBuilderControl MakeKLKTensionSCBC (
		final double tension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (tension),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
			null
		);
	}

	static final SegmentCustomBuilderControl MakePolynomialSBP (
		final int polynomialDegree)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (polynomialDegree + 1),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
			null
		);
	}

	private static final SegmentCustomBuilderControl MakeSCBC (
		final String basisSpline)
		throws Exception
	{
		if (basisSpline.equalsIgnoreCase (MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL)) {
			return new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			);
		}

		if (basisSpline.equalsIgnoreCase (MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_TENSION)) {
			return new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_TENSION,
				new ExponentialTensionSetParams (1.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			);
		}

		return null;
	}

	private static final TreeMap<Double, Double> SwapCashFlow (
		final double coupon,
		final int frequency,
		final double tenorInYears)
	{
		double inverseFrequency = 1. / frequency;
		double couponOverFrequency = coupon * inverseFrequency;

		TreeMap<Double, Double> cashflowMap = new TreeMap<Double, Double>();

		for (double cashflowTime = inverseFrequency;
			cashflowTime < tenorInYears;
			cashflowTime += inverseFrequency)
		{
			cashflowMap.put (cashflowTime, couponOverFrequency);
		}

		cashflowMap.put (0., -1.);

		cashflowMap.put (1. * tenorInYears, 1. + couponOverFrequency);

		return cashflowMap;
	}

	private static final SegmentResponseValueConstraint GenerateSegmentConstraint (
		final TreeMap<Double, Double> cashflowMap,
		final MultiSegmentSequence discountFactorMultiSegmentSequence)
		throws Exception
	{
		double value = 0.;

		List<Double> timeList = new ArrayList<Double>();

		List<Double> weightList = new ArrayList<Double>();

		for (Map.Entry<Double, Double> cashflowMapEntry : cashflowMap.entrySet()) {
			double cashflowTime = cashflowMapEntry.getKey();

			double cashflowValue = cashflowMapEntry.getValue();

			if (null != discountFactorMultiSegmentSequence &&
				discountFactorMultiSegmentSequence.in (cashflowTime))
			{
				value += discountFactorMultiSegmentSequence.responseValue (cashflowTime) * cashflowValue;
			} else {
				timeList.add (cashflowTime);

				weightList.add (cashflowValue);
			}
		}

		int size = timeList.size();

		double[] nodeArray = new double[size];
		double[] nodeWeightArray = new double[size];

		for (int i = 0; i < size; ++i) {
			nodeArray[i] = timeList.get (i);

			nodeWeightArray[i] = weightList.get (i);
		}

		return new SegmentResponseValueConstraint (nodeArray, nodeWeightArray, -1. * value);
	}

	private static final Map<Double, Double> SwapQuotes()
	{
		Map<Double, Double> swapQuotesMap = new TreeMap<Double, Double>();

		swapQuotesMap.put (4., 0.0166);

		swapQuotesMap.put (5., 0.0206);

		swapQuotesMap.put (6., 0.0241);

		swapQuotesMap.put (7., 0.0269);

		swapQuotesMap.put (8., 0.0292);

		swapQuotesMap.put (9., 0.0311);

		swapQuotesMap.put (10., 0.0326);

		swapQuotesMap.put (11., 0.0340);

		swapQuotesMap.put (12., 0.0351);

		swapQuotesMap.put (15., 0.0375);

		swapQuotesMap.put (20., 0.0393);

		swapQuotesMap.put (25., 0.0402);

		swapQuotesMap.put (30., 0.0407);

		swapQuotesMap.put (40., 0.0409);

		swapQuotesMap.put (50., 0.0409);

		return swapQuotesMap;
	}

	private static final MultiSegmentSequence BuildSwapCurve (
		MultiSegmentSequence multiSegmentSequence,
		final BoundarySettings boundarySettings,
		final int calibrationDetail)
		throws Exception
	{
		for (Map.Entry<Double, Double> swapQuoteMapEntry : SwapQuotes().entrySet()) {
			double tenorInYears = swapQuoteMapEntry.getKey();

			SegmentResponseValueConstraint segmentResponseValueConstraint = GenerateSegmentConstraint (
				SwapCashFlow (swapQuoteMapEntry.getValue(), 2, tenorInYears),
				multiSegmentSequence
			);

			if (null == multiSegmentSequence) {
				multiSegmentSequence = MultiSegmentSequenceBuilder.CreateUncalibratedStretchEstimator (
					"SWAP",
					new double[]
					{
						0.,
						tenorInYears
					},
					new SegmentCustomBuilderControl[]
					{
						MakeSCBC (MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_TENSION)
					}
				);

				multiSegmentSequence.setup (
					1.,
					new SegmentResponseValueConstraint[]
					{
						segmentResponseValueConstraint
					},
					null,
					boundarySettings,
					calibrationDetail
				);
			} else {
				multiSegmentSequence = MultiSegmentSequenceModifier.AppendSegment (
					multiSegmentSequence,
					tenorInYears,
					segmentResponseValueConstraint,
					MakeKLKTensionSCBC (1.),
					boundarySettings,
					calibrationDetail
				);
			}
		}

		return multiSegmentSequence;
	}

	private static final Map<Double, Double> CashDFQuotes()
	{
		Map<Double, Double> cashDiscountFactorQuoteMap = new TreeMap<Double, Double>();

		cashDiscountFactorQuoteMap.put (0.005556, 0.999991);

		cashDiscountFactorQuoteMap.put (0.019444, 0.999967);

		cashDiscountFactorQuoteMap.put (0.038889, 0.999931);

		cashDiscountFactorQuoteMap.put (0.083333, 0.999836);

		cashDiscountFactorQuoteMap.put (0.166667, 0.999622);

		cashDiscountFactorQuoteMap.put (0.250000, 0.999360);

		cashDiscountFactorQuoteMap.put (0.500000, 0.998686);

		cashDiscountFactorQuoteMap.put (0.750000, 0.997888);

		cashDiscountFactorQuoteMap.put (1.000000, 0.996866);

		cashDiscountFactorQuoteMap.put (1.250000, 0.995522);

		cashDiscountFactorQuoteMap.put (1.500000, 0.993609);

		cashDiscountFactorQuoteMap.put (1.750000, 0.991033);

		cashDiscountFactorQuoteMap.put (2.000000, 0.987724);

		cashDiscountFactorQuoteMap.put (2.250000, 0.983789);

		return cashDiscountFactorQuoteMap;
	}

	private static final MultiSegmentSequence BuildCashCurve (
		final BoundarySettings boundarySettings,
		final int calibrationDetail)
		throws Exception
	{
		MultiSegmentSequence cashMultiSegmentSequence =
			MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
				"CASH",
				new double[]
				{
					0.,
					0.002778
				}, // t0 and t1 for the segment
				new double[]
				{
					1.,
					0.999996
				}, // the corresponding discount factors
				new SegmentCustomBuilderControl[]
				{
					// MakeSCBC (MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_TENSION)
					MakeKLKTensionSCBC (1.)
				}, // Exponential Tension Basis Spline
				null,
				boundarySettings,
				calibrationDetail // "Natural" Spline Boundary Condition + Calibrate the full stretch
			);

		for (Map.Entry<Double, Double> cashDiscountFactorQuoteMapEntry : CashDFQuotes().entrySet()) {
			cashMultiSegmentSequence = MultiSegmentSequenceModifier.InsertKnot (
				cashMultiSegmentSequence,
				cashDiscountFactorQuoteMapEntry.getKey(),
				cashDiscountFactorQuoteMapEntry.getValue(),
				boundarySettings,
				calibrationDetail
			);
		}

		return cashMultiSegmentSequence;
	}

	private static final void CustomCurveBuilderTest()
		throws Exception
	{
		BoundarySettings naturalStandardBoundarySettings = BoundarySettings.NaturalStandard();

		BoundarySettings financialStandardBoundarySettings = BoundarySettings.FinancialStandard();

		BoundarySettings notAKnotStandardBoundarySettings = BoundarySettings.NotAKnotStandard (1, 1);

		MultiSegmentSequence naturalCashMultiSegmentSequence = BuildCashCurve (
			naturalStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		MultiSegmentSequence financialCashMultiSegmentSequence = BuildCashCurve (
			financialStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		MultiSegmentSequence notAKnotCashMultiSegmentSequence = BuildCashCurve (
			notAKnotStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		double xRightEdge = naturalCashMultiSegmentSequence.getRightPredictorOrdinateEdge();

		double xLeftEdge = naturalCashMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		double xShift = 0.1 * (xRightEdge - xLeftEdge);

		System.out.println (
			"\t||  ----------------       <====>    ------------------       <====>    ------------------"
		);

		System.out.println (
			"\t||  NATURAL BOUNDARY       <====>   NOT A KNOT BOUNDARY       <====>    FINANCIAL BOUNDARY"
		);

		System.out.println (
			"\t||  ----------------       <====>    ------------------       <====>    ------------------"
		);

		for (double x = xLeftEdge; x <= xRightEdge; x = x + xShift) {
			System.out.println (
				"\t||  Cash DF[" + FormatUtil.FormatDouble (
					x,
					1,
					3,
					1.
				) + "Y] => " + FormatUtil.FormatDouble (
					naturalCashMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + naturalCashMultiSegmentSequence.monotoneType (x) + "  <====>  " +
				FormatUtil.FormatDouble (
					notAKnotCashMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + notAKnotCashMultiSegmentSequence.monotoneType (x) + "  <====>  " +
				FormatUtil.FormatDouble (
					financialCashMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + naturalCashMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println (
			"\t||  ----------------       <====>    ------------------       <====>    ------------------"
		);

		MultiSegmentSequence naturalSwapMultiSegmentSequence = BuildSwapCurve (
			naturalCashMultiSegmentSequence,
			naturalStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		MultiSegmentSequence financialSwapMultiSegmentSequence = BuildSwapCurve (
			financialCashMultiSegmentSequence,
			financialStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		MultiSegmentSequence notAKnotSwapMultiSegmentSequence = BuildSwapCurve (
			notAKnotCashMultiSegmentSequence,
			notAKnotStandardBoundarySettings,
			MultiSegmentSequence.CALIBRATE
		);

		xRightEdge = naturalSwapMultiSegmentSequence.getRightPredictorOrdinateEdge();

		xLeftEdge = naturalSwapMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		xShift = 0.05 * (xRightEdge - xLeftEdge);

		for (double x = xLeftEdge; x <= xRightEdge; x = x + xShift) {
			System.out.println (
				"\t||  Swap DF   [" + FormatUtil.FormatDouble (
					x,
					2,
					0,
					1.
				) + "Y] => " + FormatUtil.FormatDouble (
					naturalSwapMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + naturalSwapMultiSegmentSequence.monotoneType (x) + "  <====>  " +
				FormatUtil.FormatDouble (
					notAKnotSwapMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + notAKnotSwapMultiSegmentSequence.monotoneType (x) + "  <====>  " +
				FormatUtil.FormatDouble (
					financialSwapMultiSegmentSequence.responseValue (x),
					1,
					6,
					1.
				) + " | " + financialSwapMultiSegmentSequence.monotoneType (x)
			);
		}

		System.out.println (
			"\t||  ----------------       <====>    ------------------       <====>    ------------------"
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

		CustomCurveBuilderTest();

		EnvManager.TerminateEnv();
	}
}
