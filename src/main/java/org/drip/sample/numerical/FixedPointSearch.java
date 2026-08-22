
package org.drip.sample.numerical;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1solver.*;
import org.drip.numerical.differentiation.*;
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
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>FixedPointSearch</i> contains a sample illustration of usage of the Root Finder Library. It
 * 	demonstrates the fixed point extraction using the following techniques:
 * 	- Newton-Raphson method
 * 	- Bisection Method
 * 	- False Position
 * 	- Quadratic Interpolation
 * 	- Inverse Quadratic Interpolation
 * 	- Ridder's method
 * 	- Brent's method
 * 	- Zheng's method
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

public class FixedPointSearch
{

	private static final void InvokeNewton (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderNewton (
				0.,
				function,
				true
			).findRoot();

			System.out.println ("--------\nNEWTON START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nNEWTON FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeBisection (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
				0.,
				function,
				null,
				VariateIteratorPrimitive.BISECTION,
				true
			).findRoot();

			System.out.println ("--------\nBISECTION START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nBISECTION FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeFalsePosition (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
				0.,
				function,
				null,
				VariateIteratorPrimitive.FALSE_POSITION,
				true
			).findRoot();

			System.out.println ("--------\nFALSE POSITION START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nFALSE POSITION FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeQuadraticInterpolation (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
				0.,
				function,
				null,
				VariateIteratorPrimitive.QUADRATIC_INTERPOLATION,
				true
			).findRoot();

			System.out.println ("--------\nQUADRATIC INTERPOLATION START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else
				System.out.println ("Root searched failed!");

			System.out.println ("--------\nQUADRATIC INTERPOLATION FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeInverseQuadraticInterpolation (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
				0.,
				function,
				null,
				VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION,
				true
			).findRoot();

			System.out.println ("--------\nINVERSE QUADRATIC INTERPOLATION START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nINVERSE QUADRATIC INTERPOLATION FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeRidder (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
				0.,
				function,
				null,
				VariateIteratorPrimitive.RIDDER,
				true
			).findRoot();

			System.out.println ("--------\nRIDDER START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nRIDDER FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeBrent (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBrent (
				0.,
				function,
				true
			).findRoot();

			System.out.println ("--------\nBRENT START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nBRENT FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final void InvokeZheng (
		final R1ToR1 function)
	{
		try {
			FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderZheng (
				0.,
				function,
				true
			).findRoot();

			System.out.println ("--------\nZHENG START\n-------");

			if (null != fixedPointFinderOutput && fixedPointFinderOutput.containsRoot()) {
				System.out.println (
					"Root: " + FormatUtil.FormatDouble (fixedPointFinderOutput.root(), 1, 4, 1.)
				);

				System.out.println (fixedPointFinderOutput.displayString());
			} else {
				System.out.println ("Root searched failed!");
			}

			System.out.println ("--------\nZHENG FINISH\n-------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 */

	public static final void main (
		final String[] argumentArray)
	{
		EnvManager.InitEnv ("");

		R1ToR1 function = new R1ToR1 (null)
		{
			@Override public double evaluate (
				final double variate)
				throws Exception
			{
				return Math.cos (variate) - variate * variate * variate;
			}

			@Override public Differential differential (
				final double variate,
				final double objectiveFunctionValue,
				final int order)
			{
				if (0 >= order || 2 < order) {
					return null;
				}

				double variateInfinitesimal = Double.NaN;

				try {
					variateInfinitesimal = _dc.getVariateInfinitesimal (variate);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}

				if (1 != order) {
					try {
						return new Differential (
							variateInfinitesimal,
							(-1. * Math.cos (variate) - 6. * variate) * variateInfinitesimal
						);
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}

				try {
					return new Differential (
						variateInfinitesimal,
						(-1. * Math.sin (variate) - 3. * variate * variate) * variateInfinitesimal
					);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override public double integrate (
				final double begin,
				final double end)
				throws Exception
			{
				return Integrator.Boole (this, begin, end);
			}
		};

		InvokeNewton (function);

		InvokeBisection (function);

		InvokeFalsePosition (function);

		InvokeQuadraticInterpolation (function);

		InvokeInverseQuadraticInterpolation (function);

		InvokeRidder (function);

		InvokeBrent (function);

		InvokeZheng (function);

		EnvManager.TerminateEnv();
	}
}
