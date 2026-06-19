
package org.drip.dynamics.kolmogorov;

import org.drip.dynamics.ito.R1ToR1Drift;
import org.drip.dynamics.ito.R1ToR1Volatility;
import org.drip.dynamics.ito.TimeR1Vertex;
import org.drip.dynamics.process.R1ProbabilityDensityFunction;
import org.drip.function.definition.R1ToR1;

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
 * <i>R1FokkerPlanck</i> exposes the R<sup>1</sup> Fokker-Planck Probability Density Function Evolution
 * 	Equation. The References are:
 *  
 * 	<br><br>
 *  <ul>
 * 		<li>
 * 			Bogoliubov, N. N., and D. P. Sankevich (1994): N. N. Bogoliubov and Statistical Mechanics
 * 				<i>Russian Mathematical Surveys</i> <b>49 (5)</b> 19-49
 * 		</li>
 * 		<li>
 * 			Holubec, V., K. Kroy, and S. Steffenoni (2019): Physically Consistent Numerical Solver for
 * 				Time-dependent Fokker-Planck Equations <i>Physical Review E</i> <b>99 (4)</b> 032117
 * 		</li>
 * 		<li>
 * 			Kadanoff, L. P. (2000): <i>Statistical Physics: Statics, Dynamics, and Re-normalization</i>
 * 				<b>World Scientific</b>
 * 		</li>
 * 		<li>
 * 			Ottinger, H. C. (1996): <i>Stochastic Processes in Polymeric Fluids</i> <b>Springer-Verlag</b>
 * 				Berlin-Heidelberg
 * 		</li>
 * 		<li>
 * 			Wikipedia (2019): Fokker-Planck Equation
 * 				https://en.wikipedia.org/wiki/Fokker%E2%80%93Planck_equation
 * 		</li>
 *  </ul>
 *
 * 	It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>R1FokkerPlanck</i> Constructor</li>
 * 		<li>Retrieve the Drift Function</li>
 * 		<li>Retrieve the Volatility Function</li>
 * 		<li>Compute the Next Incremental Time Derivative of the PDF</li>
 * 		<li>Compute the Temporal Probability Distribution Function, if any</li>
 * 		<li>Compute the Steady-State Probability Distribution Function, if any</li>
 * 		<li>Compute the Temporal Probability Distribution Function given the Delta 0 Starting PDF</li>
 *	<br>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/kolmogorov/README.md">Fokker Planck Kolmogorov Forward/Backward</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1FokkerPlanck
{
	private R1ToR1Drift _driftFunction = null;
	private R1ToR1Volatility _volatilityFunction = null;

	/**
	 * <i>R1FokkerPlanck</i> Constructor
	 * 
	 * @param driftFunction The Drift Function
	 * @param volatilityFunction The Volatility Function
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1FokkerPlanck (
		final R1ToR1Drift driftFunction,
		final R1ToR1Volatility volatilityFunction)
		throws Exception
	{
		if (null == (_driftFunction = driftFunction) || null == (_volatilityFunction = volatilityFunction)) {
			throw new Exception ("R1FokkerPlanck Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Drift Function
	 * 
	 * @return The Drift Function
	 */

	public R1ToR1Drift driftFunction()
	{
		return _driftFunction;
	}

	/**
	 * Retrieve the Volatility Function
	 * 
	 * @return The Volatility Function
	 */

	public R1ToR1Volatility volatilityFunction()
	{
		return _volatilityFunction;
	}

	/**
	 * Compute the Next Incremental Time Derivative of the PDF
	 * 
	 * @param probabilityDensityFunction The PDF
	 * @param timeR1Vertex The R<sup>1</sup> Time Vertex
	 * 
	 * @return Next Incremental Time Derivative of the PDF
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double pdfDot (
		final R1ProbabilityDensityFunction probabilityDensityFunction,
		final TimeR1Vertex timeR1Vertex)
		throws Exception
	{
		if (null == probabilityDensityFunction || null == timeR1Vertex) {
			throw new Exception ("R1FokkerPlanck::pdfDot => Invalid Inputs");
		}

		final double time = timeR1Vertex.t();

		return new R1ToR1 (null) {
			@Override public double evaluate (
				final double x)
				throws Exception
			{
				TimeR1Vertex localTimeR1Vertex = new TimeR1Vertex (time, x);

				return _driftFunction.drift (localTimeR1Vertex) *
					probabilityDensityFunction.density (localTimeR1Vertex);
			}
		}.derivative (timeR1Vertex.x(), 1) +  new R1ToR1 (null) {
			@Override public double evaluate (
				final double x)
				throws Exception
			{
				TimeR1Vertex localTimeR1Vertex = new TimeR1Vertex (time, x);

				double volatility = _volatilityFunction.volatility (localTimeR1Vertex);

				return 0.5 * volatility * volatility *
					probabilityDensityFunction.density (localTimeR1Vertex);
			}
		}.derivative (timeR1Vertex.x(), 2);
	}

	/**
	 * Compute the Temporal Probability Distribution Function, if any
	 * 
	 * @param intialProbabilityDensityFunction The Initial Probability Density Function
	 * 
	 * @return The Temporal Probability Distribution Function
	 */

	public R1ProbabilityDensityFunction temporalPDF (
		final R1ToR1 intialProbabilityDensityFunction)
	{
		return null;
	}

	/**
	 * Compute the Steady-State Probability Distribution Function, if any
	 * 
	 * @return The Steady-State Probability Distribution Function
	 */

	public R1ToR1 steadyStatePDF()
	{
		return null;
	}

	/**
	 * Compute the Temporal Probability Distribution Function given the Delta 0 Starting PDF
	 * 
	 * @param x0 The X Anchor for the Delta Function
	 * 
	 * @return The Temporal Probability Distribution Function given the Delta 0 Starting PDF
	 */

	public R1ProbabilityDensityFunction deltaStartTemporalPDF (
		final double x0)
	{
		return null;
	}
}
