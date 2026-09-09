
package org.drip.sample.spline;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.bspline.*;

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
 * <i>BasisMulticBSpline</i> implements Samples for the Construction and the usage of various multic basis B
 * 	Splines. It demonstrates the following:
 * 	- Construction of segment higher order B Spline from the corresponding Hat Basis Functions.
 * 	- Estimation of the derivatives and the basis envelope cumulative integrands.
 * 	- Estimation of the normalizer and the basis envelope cumulative normalized integrands.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/SplineBuilderLibrary.md">Spline Builder Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/spline/README.md">Basis Monic Multic Tension Spline</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class BasisMulticBSpline
{

	/*
	 * This sample illustrates the construction and the usage of multic basis functions, and their eventual
	 * 	response/derivative computation and comparison with the corresponding raw/processed and monic basis.
	 * 	It shows the following:
	 * 	- Construct the hyperbolic tension basis hat pair using the left predictor ordinates and the tension.
	 * 	- Construct the hyperbolic tension basis hat pair using the right predictor ordinates and the
	 * 		tension.
	 * 	- Generate the left monic basis function from the hat type, left predictor ordinates, shape control,
	 * 		and the tension parameters.
	 * 	- Generate the right monic basis function from the hat type, right predictor ordinates, shape
	 * 		control, and the tension parameters.
	 * 	- Run a response value calculation comparison across the predictor ordinates for each of the left
	 * 		basis hat and the monic basis functions.
	 * 	- Run a response value calculation comparison across the predictor ordinates for each of the right
	 * 		basis hat and the monic basis functions.
	 * 	- Construct a multic basis function using the left/right monic basis functions, and the multic order.
	 * 	- Display the multic Basis Function response as well as normalized Cumulative across the specified
	 * 		variate range.
	 */

	private static final void RunMulticBSplineTest (
		final String hatType,
		final String shapeControlType,
		final double tension,
		final int multicBSplineOrder)
		throws Exception
	{
		double[] predictorOrdinateLeftArray =
		{
			1.,
			2.,
			3.
		};
		double[] predictorOrdinateRightArray =
		{
			2.,
			3.,
			4.
		};

		TensionBasisHat[] leftTensionBasisHatArray = BasisHatPairGenerator.HyperbolicTensionHatPair (
			predictorOrdinateLeftArray[0],
			predictorOrdinateLeftArray[1],
			predictorOrdinateLeftArray[2],
			tension
		);

		TensionBasisHat[] rightTensionBasisHatArray = BasisHatPairGenerator.HyperbolicTensionHatPair (
			predictorOrdinateRightArray[0],
			predictorOrdinateRightArray[1],
			predictorOrdinateRightArray[2],
			tension
		);

		SegmentBasisFunction monicLeftSegmentBasisFunction = SegmentBasisFunctionGenerator.Monic (
			hatType,
			shapeControlType,
			predictorOrdinateLeftArray,
			2,
			tension
		);

		SegmentBasisFunction monicRightSegmentBasisFunction = SegmentBasisFunctionGenerator.Monic (
			hatType,
			shapeControlType,
			predictorOrdinateRightArray,
			2,
			tension
		);

		System.out.println ("\t||-------------------------------------------------");

		System.out.println ("\t||            X    |   LEFT   |   RIGHT  |   MONIC  ");

		System.out.println ("\t||-------------------------------------------------");

		double x = 0.5;
		double xIncrement = 0.25;

		while (x <= 4.5) {
			System.out.println (
				"\t|| Response[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
					FormatUtil.FormatDouble (leftTensionBasisHatArray[0].evaluate (x), 1, 5, 1.) + " | " +
					FormatUtil.FormatDouble (leftTensionBasisHatArray[1].evaluate (x), 1, 5, 1.) + " | " +
					FormatUtil.FormatDouble (monicLeftSegmentBasisFunction.evaluate (x), 1, 5, 1.)
			);

			x += xIncrement;
		}

		System.out.println ("\t||-------------------------------------------------");

		System.out.println ("\t||            X    |   LEFT   |   RIGHT  |   MONIC  ");

		System.out.println ("\t||-------------------------------------------------");

		x = 0.5;

		while (x <= 4.5) {
			System.out.println (
				"\t|| Response[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
				FormatUtil.FormatDouble (rightTensionBasisHatArray[0].evaluate (x), 1, 5, 1.) + " | " +
				FormatUtil.FormatDouble (rightTensionBasisHatArray[1].evaluate (x), 1, 5, 1.) + " | " +
				FormatUtil.FormatDouble (monicRightSegmentBasisFunction.evaluate (x), 1, 5, 1.)
			);

			x += xIncrement;
		}

		SegmentBasisFunction[] multicSegmentBasisFunction = SegmentBasisFunctionGenerator.MulticSequence (
			multicBSplineOrder,
			new SegmentBasisFunction[] {
				monicLeftSegmentBasisFunction,
				monicRightSegmentBasisFunction
			}
		);

		System.out.println ("\t||-------------------------------------------------");

		System.out.println ("\t||          PREDICTOR    | RESPONSE | CUMULATIVE  ");

		System.out.println ("\t||-------------------------------------------------");

		x = 0.5;
		xIncrement = 0.125;

		while (x <= 4.50) {
			System.out.println (
				"\t|| Multic[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
				FormatUtil.FormatDouble (multicSegmentBasisFunction[0].evaluate (x), 1, 5, 1.) + " | " +
				FormatUtil.FormatDouble (multicSegmentBasisFunction[0].normalizedCumulative (x), 1, 5, 1.)
			);

			x += xIncrement;
		}

		System.out.println ("\t||-------------------------------------------------\n");
	}

	/*
	 * This sample illustrates a sequence of tests using basis multic B Splines. In particular it shows the
	 * 	following:
	 * 	- Creation and usage of Multic B Spline built off of raw hyperbolic tension basis function, rational
	 * 		linear shape controller, tension = 1.0, and 3rd order multic.
	 * 	- Creation and usage of Multic B Spline built off of processed hyperbolic tension basis function,
	 * 		rational linear shape controller, tension = 1.0, and 3rd order multic.
	 * 	- Creation and usage of Multic B Spline built off of raw cubic tension basis function, rational
	 * 		linear shape controller, tension = 0.0, and 3rd order multic.
	 * 	- Creation and usage of Multic B Spline built off of raw cubic tension basis function, rational
	 * 		linear shape controller, tension = 1.0, and 3rd order multic.
	 * 	- Creation and usage of Multic B Spline built off of raw cubic tension basis function, rational
	 * 		quadratic shape controller, tension = 1.0, and 3rd order multic.
	 * 	- Creation and usage of Multic B Spline built off of raw cubic tension basis function, rational
	 * 		exponential shape controller, tension = 1.0, and 3rd order multic.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void BasisMulticBSplineSample()
		throws Exception
	{
		System.out.println (
			"\t||    RAW TENSION HYPERBOLIC | LINEAR SHAPE CONTROL | TENSION = 1.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.RAW_TENSION_HYPERBOLIC,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			1.,
			3
		);

		System.out.println (
			"\t||   PROC TENSION HYPERBOLIC | LINEAR SHAPE CONTROL | TENSION = 1.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.PROCESSED_TENSION_HYPERBOLIC,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			1.,
			3
		);

		System.out.println (
			"\t||   RAW CUBIC RATIONAL | LINEAR SHAPE CONTROL | TENSION = 0.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			0.,
			3
		);

		System.out.println (
			"\t||   RAW CUBIC RATIONAL | LINEAR SHAPE CONTROL | TENSION = 1.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			1.,
			3
		);

		System.out.println (
			"\t||   RAW CUBIC RATIONAL | QUADRATIC SHAPE CONTROL | TENSION = 1.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_QUADRATIC,
			1.,
			3
		);

		System.out.println (
			"\t||   RAW CUBIC RATIONAL | EXPONENTIAL SHAPE CONTROL | TENSION = 1.0 | CUBIC B SPLINE"
		);

		RunMulticBSplineTest (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_EXPONENTIAL,
			1.,
			3
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

		BasisMulticBSplineSample();

		EnvManager.TerminateEnv();
	}
}
