
package org.drip.optimization.neldermead;

import org.drip.numerical.common.NumberUtil;
import org.drip.service.common.FormatUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>AmoebaCoefficients</i> maintains the Reflection, the Expansion, the Contraction, and the Shrink
 * 	Coefficients employed in the Nelder-Mead Scheme. The References are:
 *  
 * <br>
 * 	<ul>
 *  	<li>
 *  		Kolda, T. G., R. M., Lewis, and V. Torczon (2003): Optimization by Direct Search: New
 *  			Perspectives on some Classical and Modern Methods <i>SIAM Review</i> <b>45 (3)</b> 385-482
 *  	</li>
 *  	<li>
 *  		Lewis, R. M., A. Shepherd, and V. Torczon (2007): Implementing Generating Set Search Methods for
 *  			Linearly Constrained Minimization <i>SIAM Journal of Scientific Computing</i> <b>29 (6)</b>
 *  			2507-2530
 *  	</li>
 * 		<li>
 * 			Nash, J. C. (1979): <i>Compact Numerical Methods: Linear Algebra and Function Minimization</i>
 * 				<b>Rutledge</b> New York NY
 * 		</li>
 * 		<li>
 * 			Press, W. H., S. A. Teukolsky, W. T. Vetterling, and B. P. Flannery (2007): <i>Numerical Recipes
 * 				in C: The Art of Scientific Computing 3<sup>rd</sup> Edition</i> <b>Cambridge University
 * 				Press</b> New York NY
 * 		</li>
 * 		<li>
 * 			Wikipedia (2026): Nelder-Mead Method https://en.wikipedia.org/wiki/Nelder%E2%80%93Mead_method
 * 		</li>
 * 	</ul>
 *
 * <br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalOptimizerLibrary.md">Numerical Optimizer Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/optimization/README.md">Necessary, Sufficient, and Regularity Checks for Gradient Descent and LP/MILP/MINLP Schemes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/optimization/neldermead/README.md">Nelder-Mead R<sup>d</sup> Function Optimization</a></td></tr>
 *  </table>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AmoebaCoefficients
{
	private double _shrink = Double.NaN;
	private double _expansion = Double.NaN;
	private double _reflection = Double.NaN;
	private double _contraction = Double.NaN;

	/**
	 * Construct a Standard Instance of <i>AmoebaCoefficients</i>
	 * 
	 * @return Standard Instance of <i>AmoebaCoefficients</i>
	 */

	public static final AmoebaCoefficients Standard()
	{
		try {
			return new AmoebaCoefficients (1., 2., 0.5, 0.5);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>AmoebaCoefficients</i> Constructor
	 * 
	 * @param reflection Reflection Coefficient (Alpha)
	 * @param expansion Expansion Coefficient (Gamma)
	 * @param contraction Contraction Coefficient (Rho)
	 * @param shrink Shrink Coefficient (Sigma)
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public AmoebaCoefficients (
		final double reflection,
		final double expansion,
		final double contraction,
		final double shrink)
		throws Exception
	{
		if (!NumberUtil.IsValid (_reflection = reflection) || 0. >= _reflection ||
			!NumberUtil.IsValid (_expansion = expansion) || 1. >= _expansion ||
			!NumberUtil.IsValid (_contraction = contraction) || 0. >= _contraction || 0.5 < _contraction ||
			!NumberUtil.IsValid (_shrink = shrink))
		{
			throw new Exception ("AmoebaCoefficients Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Reflection Coefficient
	 * 
	 * @return Reflection Coefficient
	 */

	public double reflection()
	{
		return _reflection;
	}

	/**
	 * Retrieve the Expansion Coefficient (Gamma)
	 * 
	 * @return Expansion Coefficient (Gamma)
	 */

	public double expansion()
	{
		return _expansion;
	}

	/**
	 * Retrieve the Contraction Coefficient (Rho)
	 * 
	 * @return Contraction Coefficient (Rho)
	 */

	public double contraction()
	{
		return _contraction;
	}

	/**
	 * Retrieve the Shrink Coefficient (Sigma)
	 * 
	 * @return Shrink Coefficient (Sigma)
	 */

	public double shrink()
	{
		return _shrink;
	}

	/**
	 * Retrieve the Reflection Coefficient (Alpha)
	 * 
	 * @return Reflection Coefficient (Alpha)
	 */

	public double alpha()
	{
		return _reflection;
	}

	/**
	 * Retrieve the Expansion Coefficient
	 * 
	 * @return Expansion Coefficient
	 */

	public double gamma()
	{
		return _expansion;
	}

	/**
	 * Retrieve the Contraction Coefficient
	 * 
	 * @return Contraction Coefficient
	 */

	public double rho()
	{
		return _contraction;
	}

	/**
	 * Retrieve the Shrink Coefficient
	 * 
	 * @return Shrink Coefficient
	 */

	public double sigma()
	{
		return _shrink;
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @param prefix The JSON Prefix
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public String toString (
		final String prefix)
	{
		return prefix + "[" +
			"Reflection: " + FormatUtil.FormatDouble (_reflection, 1, 4, 1.) + "; " +
			"Expansion: " + FormatUtil.FormatDouble (_expansion, 1, 4, 1.) + "; " +
			"Contraction: " + FormatUtil.FormatDouble (_contraction, 1, 4, 1.) + "; " +
			"Shrink: " + FormatUtil.FormatDouble (_shrink, 1, 4, 1.) +
		"]";
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public @Override String toString()
	{
		return toString ("");
	}
}
