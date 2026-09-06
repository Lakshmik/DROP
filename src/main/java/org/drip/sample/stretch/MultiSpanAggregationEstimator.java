
package org.drip.sample.stretch;

import java.util.*;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.grid.*;
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
 * <i>MultiSpanAggregationEstimator</i> demonstrates the Construction and Usage of the Multiple Span
 * 	Aggregation Functionality.
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

public class MultiSpanAggregationEstimator
{

	private static final SegmentCustomBuilderControl PolynomialSegmentControlParams (
		final int basisCount,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (basisCount),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
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

		double[] xArray =
		{
			 1.0,
			 1.5,
			 2.0,
			 3.0,
			 4.0,
			 5.0,
			 6.5,
			 8.0,
			10.0
		};
		double[] y1Array =
		{
			25.00,
			20.25,
			16.00,
			 9.00,
			 4.00,
			 1.00,
			 0.25,
			 4.00,
			16.00
		};
		double[] y2Array = 
		{
			27.00,
			22.25,
			18.00,
			11.00,
			 6.00,
			 3.00,
			 2.25,
			 6.00,
			18.00
		};

		SegmentCustomBuilderControl segmentCustomBuilderControl = PolynomialSegmentControlParams (
			4,
			SegmentInelasticDesignControl.Create (2, 2),
			null
		);

		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] aSCBC = new SegmentCustomBuilderControl[segmentCount]; 

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			aSCBC[segmentIndex] = segmentCustomBuilderControl;
		}

		MultiSegmentSequence multiSegmentSequence1 =
			MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
				"SPLINE_STRETCH_1", 				// Name
				xArray, 							// predictors
				y1Array, 							// responses
				aSCBC, 								// Basis Segment Builder parameters
				null,  								// NULL segment Best Fit Response
				BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
			);

		MultiSegmentSequence multiSegmentSequence2 =
			MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
				"SPLINE_STRETCH_2", 				// Name
				xArray, 							// predictors
				y2Array, 							// responses
				aSCBC, 								// Basis Segment Builder parameters
				null,  								// NULL segment Best Fit Response
				BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
			);

		List<Double> weightList = new ArrayList<Double>();

		weightList.add (0.14);

		weightList.add (0.71);

		List<Span> spanList = new ArrayList<Span>();

		spanList.add (new OverlappingStretchSpan (multiSegmentSequence1));

		spanList.add (new OverlappingStretchSpan (multiSegmentSequence2));

		AggregatedSpan aggregatedSpan = new AggregatedSpan (spanList, weightList);

		double x = 1.;
		double xMaximum = 10.;

		System.out.println ("\t||------------------------------------------------------------------------");

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Y[" + x + "] =>" + FormatUtil.FormatDouble (
					aggregatedSpan.calcResponseValue (x),
					2,
					2,
					1.
				) + " | " + FormatUtil.FormatDouble (
					0.14 * multiSegmentSequence1.responseValue (x) +
						0.71 * multiSegmentSequence2.responseValue (x),
					2,
					2,
					1.
				)
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
