
package org.drip.function.r1tor1custom;

import org.drip.dynamics.meanreverting.CKLSParameters;
import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.specialfunction.gamma.NemesAnalytic;

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
 * <i>CIRPDF</i> exposes the R<sup>1</sup> Univariate Cox-Ingersoll-Ross Probability Density Function. The
 *  References are:
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
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Construct a Standard Instance of <i>CIRPDF</i></li>
 * 		<li><i>CIRPDF</i> Constructor</li>
 * 		<li>Retrieve Alpha</li>
 * 		<li>Retrieve Beta</li>
 * 		<li>Retrieve the Gamma Function</li>
 * 		<li>Evaluate for the given r</li>
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

public class CIRPDF
	extends R1ToR1
{
	private double _beta = Double.NaN;
	private double _alpha = Double.NaN;
	private R1ToR1 _gammaFunction = null;

	/**
	 * Construct a Standard Instance of <i>CIRPDF</i>
	 * 
	 * @param cklsParameters The CKLS Parameters
	 * 
	 * @return Standard Instance of <i>CIRPDF</i>
	 */

	public static final CIRPDF Standard (
		final CKLSParameters cklsParameters)
	{
		if (null == cklsParameters) {
			return null;
		}

		double inverseVolatility = 1. / cklsParameters.volatilityCoefficient();

		double beta = 2. * cklsParameters.meanReversionSpeed()* inverseVolatility * inverseVolatility;

		try {
			return new CIRPDF (beta * cklsParameters.meanReversionLevel(), beta, new NemesAnalytic (null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>CIRPDF</i> Constructor
	 * 
	 * @param alpha The Alpha
	 * @param beta The Beta
	 * @param gammaFunction The Gamma Function
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public CIRPDF (
		final double alpha,
		final double beta,
		final R1ToR1 gammaFunction)
		throws Exception
	{
		super (null);

		if (!NumberUtil.IsValid (_alpha = alpha) || !NumberUtil.IsValid (_beta = beta) ||
			null == (_gammaFunction = gammaFunction))
		{
			throw new Exception ("CIRPDF Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve Alpha
	 * 
	 * @return The Alpha
	 */

	public double alpha()
	{
		return _alpha;
	}

	/**
	 * Retrieve Beta
	 * 
	 * @return The Beta
	 */

	public double beta()
	{
		return _beta;
	}

	/**
	 * Retrieve the Gamma Function
	 * 
	 * @return The Gamma Function
	 */

	public R1ToR1 gammaFunction()
	{
		return _gammaFunction;
	}

	/**
	 * Evaluate for the given r
	 * 
	 * @param r R
	 *  
	 * @return Returns the calculated value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	@Override public double evaluate (
		final double r)
		throws Exception
	{
		if (!NumberUtil.IsValid (r)) {
			throw new Exception ("CIRPDF::evaluate => Invalid Inputs");
		}

		return Math.pow (_beta, _alpha) * Math.pow (r, _alpha - 1.) * Math.exp (-1. * _beta * r) /
			_gammaFunction.evaluate (_alpha);
	}
}
