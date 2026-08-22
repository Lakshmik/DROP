
package org.drip.sample.numerical;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1.*;
import org.drip.numerical.r1integration.Integrator;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * <i>IntegrandQuadrature</i> shows samples for the following routines for integrating the objective function:
 * 	- Mid-Point Scheme
 * 	- Trapezoidal Scheme
 * 	- Simpson/Simpson38 schemes
 * 	- Boole Scheme
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/numerical/README.md">Search, Quadratures, Fourier Phase Tracker</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class IntegrandQuadrature
{

	private static void ComputeQuadrature (
		final R1ToR1 function,
		final double actual,
		final double start,
		final double end)
		throws Exception
	{
		int rightDecimal = 8;

		System.out.println ("\t\tActual      : " + FormatUtil.FormatDouble (actual, 1, rightDecimal, 1.));

		System.out.println (
			"\t\tLinear      : " + FormatUtil.FormatDouble (
				Integrator.LinearQuadrature (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);

		System.out.println (
			"\t\tMidPoint     : " + FormatUtil.FormatDouble (
				Integrator.MidPoint (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);

		System.out.println (
			"\t\tTrapezoidal  : " + FormatUtil.FormatDouble (
				Integrator.Trapezoidal (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);

		System.out.println (
			"\t\tSimpson      : " + FormatUtil.FormatDouble (
				Integrator.Simpson (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);

		System.out.println (
			"\t\tSimpson 38   : " + FormatUtil.FormatDouble (
				Integrator.Simpson38 (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);

		System.out.println (
			"\t\tBoole        : " + FormatUtil.FormatDouble (
				Integrator.Boole (function, start, end),
				1,
				rightDecimal,
				1.
			)
		);
	}

	private static void IntegrandQuadratureSample()
		throws Exception
	{
		double start = 0.;
		double end = 1.;

		R1ToR1 exponentialTensionFunction = new ExponentialTension (Math.E, 1.);

		System.out.println ("\n\t-------------------------------------\n");

		ComputeQuadrature (
			exponentialTensionFunction,
			exponentialTensionFunction.evaluate (end) - exponentialTensionFunction.evaluate (start),
			start,
			end
		);

		System.out.println ("\n\t-------------------------------------\n");

		ComputeQuadrature (
			new R1ToR1 (null)
			{
				@Override public double evaluate (
					final double variate)
					throws Exception
				{
					return Math.cos (variate) - variate * variate * variate;
				}
			},
			Math.sin (end) - Math.sin (start) -
				0.25 * (end * end * end * end - start * start * start * start),
			start,
			end
		);

		System.out.println ("\n\t-------------------------------------\n");

		ComputeQuadrature (
			new R1ToR1 (null)
			{
				@Override public double evaluate (
					final double variate)
					throws Exception
				{
					return variate * variate * variate - 3. * variate * variate + 2. * variate;
				}
			},
			0.25 * (end * end * end * end - start * start * start * start) -
				(end * end * end - start * start * start) + (end * end - start * start),
			start,
			end
		);

		System.out.println ("\n\t-------------------------------------\n");
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		IntegrandQuadratureSample();

		EnvManager.TerminateEnv();
	}
}
