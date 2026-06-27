
package org.drip.sample.hullwhite;

import org.drip.analytics.date.*;
import org.drip.dynamics.hullwhite.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.FundingLabel;

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
 * <i>TrinomialTreeEvolution</i> demonstrates the Construction and Usage of the Hull-White Trinomial Tree and
 * 	the Eventual Evolution of the Short Rate on it.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/hullwhite/README.md">Hull White Trinomial Tree Dynamics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class TrinomialTreeEvolution
{

	private static final SingleFactorStateEvolver HullWhiteEvolver (
		final String currency,
		final double sigma,
		final double a,
		final double startingForwardRate)
		throws Exception
	{
		return new SingleFactorStateEvolver (
			FundingLabel.Standard (currency),
			sigma,
			a,
			new Flat (startingForwardRate),
			new BoxMullerGaussian (0., 1.)
		);
	}

	private static final void DumpMetrics (
		final TrinomialTreeTransitionMetrics trinomialTreeTransitionMetrics)
		throws Exception
	{
		System.out.println (
			"\t| [" + new JulianDate (
				trinomialTreeTransitionMetrics.initialDate()
			) + " -> " + new JulianDate (
				trinomialTreeTransitionMetrics.terminalDate()
			) + "] => " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.expectedTerminalX(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				Math.sqrt (trinomialTreeTransitionMetrics.xVariance()),
				1,
				2,
				100.
			) + "% | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.xStochasticShift(),
				1,
				4,
				1.
			) + " || " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.probabilityUp(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.upNodeMetrics().x(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.upNodeMetrics().shortRate(),
				1,
				2,
				100.
			) + "% || " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.probabilityDown(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.downNodeMetrics().x(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.downNodeMetrics().shortRate(),
				1,
				2,
				100.
			) + "% || " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.probabilityStay(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.stayNodeMetrics().x(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				trinomialTreeTransitionMetrics.stayNodeMetrics().shortRate(),
				1,
				2,
				100.
			) + "% ||"
		);
	}

	private static final void TreeHeader (
		final String evolutionComment)
		throws Exception
	{
		System.out.println (
			"\n\n\t|----------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|                                                                                                                                                    ||"
		);

		System.out.println (evolutionComment);

		System.out.println (
			"\t|    ---------------------------------------------------                                                                                             ||"
		);

		System.out.println (
			"\t|                                                                                                                                                    ||"
		);

		System.out.println (
			"\t|    L->R:                                                                                                                                           ||"
		);

		System.out.println (
			"\t|                                                                                                                                                    ||"
		);

		System.out.println (
			"\t|        Initial Date                                                                                                                                ||"
		);

		System.out.println (
			"\t|        Final Date                                                                                                                                  ||"
		);

		System.out.println (
			"\t|        Expected Final X                                                                                                                            ||"
		);

		System.out.println (
			"\t|        X Volatility (%)                                                                                                                            ||"
		);

		System.out.println (
			"\t|        X Stochastic Shift                                                                                                                          ||"
		);

		System.out.println (
			"\t|        Move-Up Probability                                                                                                                         ||"
		);

		System.out.println (
			"\t|        Move-Up X Node Value                                                                                                                        ||"
		);

		System.out.println (
			"\t|        Move-Up Short Rate                                                                                                                          ||"
		);

		System.out.println (
			"\t|        Move-Down Probability                                                                                                                       ||"
		);

		System.out.println (
			"\t|        Move-Down X Node Value                                                                                                                      ||"
		);

		System.out.println (
			"\t|        Move-Down Short Rate                                                                                                                        ||"
		);

		System.out.println (
			"\t|        Stay Probability                                                                                                                            ||"
		);

		System.out.println (
			"\t|        Stay X Node Value                                                                                                                           ||"
		);

		System.out.println (
			"\t|        Stay Short Rate                                                                                                                             ||"
		);

		System.out.println (
			"\t|                                                                                                                                                    ||"
		);

		System.out.println (
			"\t|----------------------------------------------------------------------------------------------------------------------------------------------------||"
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

		JulianDate spotDate = DateUtil.Today();

		double startingShortRate = 0.05;
		String currency = "USD";
		double sigma = 0.01;
		double a = 1.;

		SingleFactorStateEvolver singleFactorStateEvolver = HullWhiteEvolver (
			currency,
			sigma,
			a,
			startingShortRate
		);

		int spotDateJulian = spotDate.julian();

		int finalDate = spotDate.addMonths (6).julian();

		int initialDate = spotDateJulian;
		TrinomialTreeTransitionMetrics trinomialTreeTransitionMetrics = null;

		TreeHeader (
			"\t|    Hull-White Trinomial Tree Upwards Evolution Metrics                                                                                             ||"
		);

		while (initialDate < finalDate) {
			DumpMetrics (
				trinomialTreeTransitionMetrics = singleFactorStateEvolver.evolveTrinomialTree (
					spotDateJulian,
					initialDate,
					finalDate,
					null == trinomialTreeTransitionMetrics ?
						null : trinomialTreeTransitionMetrics.upNodeMetrics()
				)
			);

			initialDate += 10;
		}

		System.out.println (
			"\t|----------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		trinomialTreeTransitionMetrics = null;
		initialDate = spotDateJulian;

		TreeHeader (
			"\t|    Hull-White Trinomial Tree Downwards Evolution Metrics                                                                                           ||"
		);

		while (initialDate < finalDate) {
			DumpMetrics (
				trinomialTreeTransitionMetrics = singleFactorStateEvolver.evolveTrinomialTree (
					spotDateJulian,
					initialDate,
					finalDate,
					null == trinomialTreeTransitionMetrics ?
						null : trinomialTreeTransitionMetrics.downNodeMetrics()
				)
			);

			initialDate += 10;
		}

		System.out.println (
			"\t|----------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		trinomialTreeTransitionMetrics = null;
		initialDate = spotDateJulian;

		TreeHeader (
			"\t|    Hull-White Trinomial Tree Stay-Put Evolution Metrics                                                                                            ||"
		);

		while (initialDate < finalDate) {
			DumpMetrics (
				trinomialTreeTransitionMetrics = singleFactorStateEvolver.evolveTrinomialTree (
					spotDateJulian,
					initialDate,
					finalDate,
					null == trinomialTreeTransitionMetrics ?
						null : trinomialTreeTransitionMetrics.stayNodeMetrics()
				)
			);

			initialDate += 10;
		}

		System.out.println (
			"\t|----------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		EnvManager.TerminateEnv();
	}
}
