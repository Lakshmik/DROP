
package org.drip.function.r1tor1custom;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;

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
 * <i>LinearRationalShapeControl</i> implements the deterministic rational shape control functionality on top
 * 	of the estimator basis splines inside - [0,...,1) - Globally [x_0,...,x_1):
 *	<br><br>
 * 			y = 1 / [1 + lambda * x]
 *	<br><br>
 *		where x is the normalized ordinate mapped as
 * 
 * 			x === (x - x_i-1) / (x_i - x_i-1)
 *
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>LinearRationalShapeControl</i> Constructor</li>
 * 		<li>Evaluate for the Given x</li>
 * 		<li>Calculate the Derivative as a Double</li>
 * 		<li>Integrate over the given Range</li>
 * 		<li>Retrieve the Shape Control Coefficient</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/README.md">Built-in R<sup>1</sup> To R<sup>1</sup> Custom Functions</a></td></tr>
 *  </table>
 *	<br>
 *  
 * @author Lakshmi Krishnamurthy
 */

public class LinearRationalShapeControl
	extends R1ToR1
{
	private double _lambda = Double.NaN;

	/**
	 * <i>LinearRationalShapeControl</i> Constructor
	 * 
	 * @param lambda Tension Parameter
	 * 
	 * @throws Exception Thrown if the inputs are invalid
	 */

	public LinearRationalShapeControl (
		final double lambda)
		throws Exception
	{
		super (null);

		if (!NumberUtil.IsValid (_lambda = lambda)) {
			throw new Exception ("LinearRationalShapeControl Constructor: Invalid tension");
		}
	}

	/**
	 * Evaluate for the Given x
	 * 
	 * @param x x
	 *  
	 * @return Returns the calculated value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	@Override public double evaluate (
		final double x)
		throws Exception
	{
		return 1. / (1. + _lambda * x);
	}

	/**
	 * Calculate the Derivative as a Double
	 * 
	 * @param x x at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	@Override public double derivative (
		final double x,
		final int order)
		throws Exception
	{
		if (!NumberUtil.IsValid (x)) {
			throw new Exception ("LinearRationalShapeControl::derivative => Invalid Inputs");
		}

		double derivativeScaler = 1. / (1. + _lambda * x);
		double derivative = derivativeScaler;

		for (int subDerivativeIndex = 0; subDerivativeIndex < order; ++subDerivativeIndex) {
			derivative *= (-1. * _lambda * derivativeScaler);
		}

		return derivative;
	}

	/**
	 * Integrate over the given Range
	 * 
	 * @param begin Range Begin 
	 * @param end Range End 
	 *  
	 * @return The Integrated Value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	@Override public double integrate (
		final double begin,
		final double end)
		throws Exception
	{
		if (!NumberUtil.IsValid (begin) || !NumberUtil.IsValid (end)) {
			throw new Exception ("LinearRationalShapeControl::integrate => Invalid Inputs");
		}

		return (Math.log ((1. + _lambda * end) / (1. + _lambda * begin))) / _lambda;
	}

	/**
	 * Retrieve the Shape Control Coefficient
	 * 
	 * @return Shape Control Coefficient
	 */

	public double shapeControlCoefficient()
	{
		return _lambda;
	}
}
