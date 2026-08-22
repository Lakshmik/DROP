
package org.drip.function.r1tor1solver;

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
 * <i>VariateIterationSelectorParams</i> implements the control parameters for the compound variate selector
 * 	Scheme used in Brent's method. Brent's method uses the following fields in
 *  <i>VariateIterationSelectorParams</i> to generate the next variate:
 * 	<br>
 * 	<ul>
 * 		<li>The Variate Primitive that is regarded as the "fast" method</li>
 * 		<li>The Variate Primitive that is regarded as the "robust" method</li>
 * 		<li>The relative variate shift that determines when the "robust" method is to be invoked over the "fast"</li>
 * 		<li>The lower bound on the variate shift between iterations that serves as the fall-back to the "robust"</li>
 * 	</ul>
 * 
 * 	It may be readily enhanced to accommodate additional primitives. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Default <i>VariateIterationSelectorParams</i> Constructor</li>
 * 		<li><i>VariateIterationSelectorParams</i> Constructor</li>
 * 		<li>Retrieve the Relative Variate Shift</li>
 * 		<li>Retrieve the Variate Shift Lower Bound</li>
 * 		<li>Retrieve the Variate Iterator Primitive meant for Speed</li>
 * 		<li>Retrieve the Variate Iterator Primitive meant for Robustness</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/r1tor1solver/README.md">Built-in R<sup>1</sup> To R<sup>1</sup> Solvers</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class VariateIterationSelectorParams
{
	private int _fastIteratorPrimitive = -1;
	private int _robustIteratorPrimitive = -1;
	private double _relativeShift = Double.NaN;
	private double _shiftLowerBound = Double.NaN;

	/**
	 * Default <i>VariateIterationSelectorParams</i> Constructor
	 */

	public VariateIterationSelectorParams()
	{
		_relativeShift = 0.5;
		_shiftLowerBound = 0.01;
		_robustIteratorPrimitive = VariateIteratorPrimitive.BISECTION;
		_fastIteratorPrimitive = VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION;
	}

	/**
	 * <i>VariateIterationSelectorParams</i> Constructor
	 * 
	 * @param relativeShift Relative Variate Shift
	 * @param shiftLowerBound Variant Shift Lower Bound
	 * @param fastIteratorPrimitive Fast Iterator Primitive
	 * @param robustIteratorPrimitive Robust Iterator Primitive
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public VariateIterationSelectorParams (
		final double relativeShift,
		final double shiftLowerBound,
		final int fastIteratorPrimitive,
		final int robustIteratorPrimitive)
		throws Exception
	{
		if (!NumberUtil.IsValid (_relativeShift = relativeShift) ||
			!NumberUtil.IsValid (_shiftLowerBound = shiftLowerBound))
		{
			throw new Exception ("VariateIterationSelectorParams Constructor: Invalid inputs!");
		}
	}

	/**
	 * Retrieve the Relative Variate Shift
	 * 
	 * @return Relative Variate Shift
	 */

	public double relativeShift()
	{
		return _relativeShift;
	}

	/**
	 * Retrieve the Variate Shift Lower Bound
	 * 
	 * @return Variate Shift Lower Bound
	 */

	public double shiftLowerBound()
	{
		return _shiftLowerBound;
	}

	/**
	 * Retrieve the Variate Iterator Primitive meant for Speed
	 * 
	 * @return Variate Iterator Primitive meant for Speed
	 */

	public int fastIteratorPrimitive()
	{
		return _fastIteratorPrimitive;
	}

	/**
	 * Retrieve the Variate Iterator Primitive meant for Robustness
	 * 
	 * @return Variate Iterator Primitive meant for Robustness
	 */

	public int robustVariateIteratorPrimitive()
	{
		return _robustIteratorPrimitive;
	}
}
