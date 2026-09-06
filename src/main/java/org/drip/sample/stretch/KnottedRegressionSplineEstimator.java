
package org.drip.sample.stretch;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;

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
 * <i>KnottedRegressionSplineEstimator</i> shows the sample construction and usage of Knot-based Regression
 * 	Splines. It demonstrates construction of the segment's predictor ordinate/response value combination, and
 * 	eventual calibration.
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

public class KnottedRegressionSplineEstimator
{

	private static final SegmentCustomBuilderControl PolynomialSegmentControlParams (
		final int basisCount,
		final SegmentInelasticDesignControl segmentInelasticDesignControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (basisCount),
			segmentInelasticDesignControl,
			null,
			null
		);
	}

	private static final void BasisSplineStretchTest (
		final double[] xArray,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final StretchBestFitResponse stretchBestFitResponse)
		throws Exception
	{
		double x = 1.;
		double xMaximum = 10.;
		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[segmentCount]; 

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentCustomBuilderControlArray[segmentIndex] = segmentCustomBuilderControl;
		}

		MultiSegmentSequence multiSegmentSequence =
			MultiSegmentSequenceBuilder.CreateRegressionSplineEstimator (
				"SPLINE_STRETCH",
				xArray, 							// predictors
				segmentCustomBuilderControlArray, 	// Basis Segment Builder parameters
				stretchBestFitResponse,
				BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
			);

		System.out.println ("\t||------------------------------------------------------------------------");

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Y[" + x + "] " + FormatUtil.FormatDouble (
					multiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + multiSegmentSequence.monotoneType (x)
			);

			System.out.println (
				"\t|| Jacobian Y[" + x + "]: " +
					multiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t|| SPLINE_STRETCH DPE: " + multiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");
	}

	private static final void RegressionSplineEstimatorTest()
		throws Exception
	{
		BasisSplineStretchTest (
			new double[]
			{
				 1.,
				 5.,
				10.
			},
			PolynomialSegmentControlParams (
				4,
				SegmentInelasticDesignControl.Create (2, 2)
			),
			StretchBestFitResponse.Create (
				new double[]
				{
					2.28,
					2.52,
					2.73,
					3.00,
					5.50,
					8.44,
					8.76,
					9.08,
					9.80,
					9.92
				},
				new double[]
				{
					14.27,
					12.36,
					10.61,
					 9.25,
					-0.50,
					 7.92,
					10.07,
					12.23,
					15.51,
					16.36
				},
				new double[]
				{
					1.09,
					0.82,
					1.34,
					1.10,
					0.50,
					0.79,
					0.65,
					0.49,
					0.24,
					0.21
				}
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

		RegressionSplineEstimatorTest();

		EnvManager.TerminateEnv();
	}
}
