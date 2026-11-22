
package org.drip.function.definition;

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
 * <i>R1R1ToR1</i> interface exposes the stubs for the evaluation of the objective function and its
 * 	derivatives for a R<sup>1</sup> Deterministic + R<sup>1</sup> Random To R<sup>1</sup> Stochastic Function
 * 	with one Random Component. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Evaluate a Single Realization for the given Variate</li>
 * 		<li>Evaluate the Expectation for the given Variate</li>
 * 		<li>Evaluate the Derivative for a Single Realization for the given Variate</li>
 * 		<li>Evaluate the Derivative Expectation at the given Variate</li>
 * 		<li>Evaluate a Path-wise Integral between the Variates</li>
 * 		<li>Evaluate the Expected Path-wise Integral between the Variates</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/definition/README.md">Function Implementation Ancillary Support Objects</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public interface R1R1ToR1
{

	/**
	 * Evaluate a Single Realization for the given Variate
	 * 
	 * @param variate Variate
	 *  
	 * @return Return the Single Realization for the given variate
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double evaluateRealization (
		final double variate)
		throws Exception;

	/**
	 * Evaluate the Expectation for the given Variate
	 * 
	 * @param variate Variate
	 *  
	 * @return Return the Expectation for the given variate
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double evaluateExpectation (
		final double variate)
		throws Exception;

	/**
	 * Evaluate the Derivative for a Single Realization for the given Variate
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 *  
	 * @return Return the Derivative for a Single Realization for the given variate
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double derivativeRealization (
		final double variate,
		final int order)
		throws Exception;

	/**
	 * Evaluate the Derivative Expectation at the given Variate
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 *  
	 * @return Return the Derivative Expectation at the given variate
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double derivativeExpectation (
		final double variate,
		final int order)
		throws Exception;

	/**
	 * Evaluate a Path-wise Integral between the Variates
	 * 
	 * @param start Variate Start
	 * @param end Variate End
	 *  
	 * @return The Path-wise Integral between the Variates
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double integralRealization (
		final double start,
		final double end)
		throws Exception;

	/**
	 * Evaluate the Expected Path-wise Integral between the Variates
	 * 
	 * @param start Variate Start
	 * @param end Variate End
	 *  
	 * @return The Expected Path-wise Integral between the Variates
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double integralExpectation (
		final double start,
		final double end)
		throws Exception;
}
