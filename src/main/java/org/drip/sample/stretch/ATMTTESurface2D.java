
package org.drip.sample.stretch;

import java.util.TreeMap;

import org.drip.service.common.FormatUtil;
import org.drip.service.common.StringUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.grid.*;
import org.drip.spline.multidimensional.WireSurfaceStretch;
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
 * <i>ATMTTESurface2D</i> demonstrates the Surface 2D ATM/TTE (X/Y) Stretch Construction and usage API.
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

public class ATMTTESurface2D
{

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

		double[] atmFactorArray =
		{
			0.8,
			0.9,
			1.0,
			1.1,
			1.2
		};
		double[] tteArray =
		{
			1.,
			2.,
			3.,
			4.,
			5.
		};
		double[][] impliedVolatilityGrid =
		{
			{
				0.44,
				0.38,
				0.33,
				0.27,
				0.25
			},
			{
				0.41,
				0.34,
				0.30,
				0.22,
				0.27
			},
			{
				0.36,
				0.31,
				0.28,
				0.30,
				0.37
			},
			{
				0.38,
				0.31,
				0.34,
				0.40,
				0.47
			},
			{
				0.43,
				0.46,
				0.48,
				0.52,
				0.57
			}
		};

		SegmentCustomBuilderControl spanSegmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);

		TreeMap<Double, Span> spanMap = new TreeMap<Double, Span>();

		SegmentCustomBuilderControl[] spanSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[atmFactorArray.length - 1];

		for (int atmSpanIndex = 0;
			atmSpanIndex < spanSegmentCustomBuilderControlArray.length;
			++atmSpanIndex)
		{
			spanSegmentCustomBuilderControlArray[atmSpanIndex] = spanSegmentCustomBuilderControl;
		}

		for (int atmSpanIndex = 0; atmSpanIndex < atmFactorArray.length; ++atmSpanIndex) {
			spanMap.put (
				atmFactorArray[atmSpanIndex],
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						"Stretch@" + tteArray + "@" + StringUtil.GUID(),
						tteArray,
						impliedVolatilityGrid[atmSpanIndex],
						spanSegmentCustomBuilderControlArray,
						null,
						BoundarySettings.NaturalStandard(),
						MultiSegmentSequence.CALIBRATE
					)
				)
			);
		}

		WireSurfaceStretch wireSurfaceStretch = new WireSurfaceStretch (
			"SurfaceStretch@" + StringUtil.GUID(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			),
			spanMap
		);

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|----------------- INPUT  SURFACE  RECOVERY -----------------|");

		System.out.print (
			"\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>"
		);

		for (double tte : tteArray) {
			System.out.print ("   " + FormatUtil.FormatDouble (tte, 1, 2, 1.) + " ");
		}

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double atmFactor : atmFactorArray) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (atmFactor, 1, 2, 1.) + "    =>");

			for (double tte : tteArray) {
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						wireSurfaceStretch.responseValue (atmFactor, tte),
						2,
						2,
						100.
					) + "%"
				);
			}

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");

		atmFactorArray = new double[]
		{
			0.850,
			0.925,
			1.000,
			1.075,
			1.150
		};
		tteArray = new double[]
		{
			1.50,
			2.25,
			3.00,
			3.75,
			4.50
		};

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|------------- IN-SURFACE RESPONSE CALCULATION --------------|");

		System.out.print (
			"\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>"
		);

		for (double tte : tteArray) {
			System.out.print ("   " + FormatUtil.FormatDouble (tte, 1, 2, 1.) + " ");
		}

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double atmFactor : atmFactorArray) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (atmFactor, 1, 2, 1.) + "    =>");

			for (double tte : tteArray) {
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						wireSurfaceStretch.responseValue (atmFactor, tte),
						2,
						2,
						100.
					) + "%"
				);
			}

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");

		atmFactorArray = new double[]
		{
			0.70,
			0.85,
			1.00,
			1.15,
			1.30
		};
		tteArray = new double[]
		{
			0.50,
			1.75,
			3.00,
			4.25,
			5.50
		};

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|------------- OFF-SURFACE RESPONSE CALCULATION -------------|");

		System.out.print (
			"\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>"
		);

		for (double tte : tteArray) {
			System.out.print ("   " + FormatUtil.FormatDouble (tte, 1, 2, 1.) + " ");
		}

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double atmFactor : atmFactorArray) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (atmFactor, 1, 2, 1.) + "    =>");

			for (double tte : tteArray) {
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						wireSurfaceStretch.responseValue (atmFactor, tte),
						2,
						2,
						100.
					) + "%"
				);
			}

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
