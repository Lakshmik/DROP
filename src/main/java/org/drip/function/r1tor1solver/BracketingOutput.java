
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
 * <i>BracketingOutput</i> carries the results of the bracketing initialization. In addition to the fields of
 * 	<i>ExecutionInitializationOutput</i>, <i>BracketingOutput</i> holds the left/right bracket variates and
 * 	the corresponding values for the objective function. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Default <i>BracketingControlParams</i> Constructor</li>
 * 		<li>Default <i>BracketingOutput</i> constructor: Initializes the output</li>
 * 		<li>Return the Left Variate</li>
 * 		<li>Return the Right Variate</li>
 * 		<li>Return the Left Objective Function</li>
 * 		<li>Return the Right Objective Function</li>
 * 		<li>Set the Brackets in the Output Object</li>
 * 		<li>Make a <i>ConvergenceOutput</i> Instance for the Open Method from the Bracketing Output</li>
 * 		<li>Return a String Form of the Bracketing Output</li>
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

public class BracketingOutput
	extends ExecutionInitializationOutput
{
	private double _variateLeft = Double.NaN;
	private double _variateRight = Double.NaN;
	private double _objectiveFunctionLeft = Double.NaN;
	private double _objectiveFunctionRight = Double.NaN;

	/**
	 * Default <i>BracketingOutput</i> constructor: Initializes the output
	 */

	public BracketingOutput()
	{
		super();
	}

	/**
	 * Return the Left Variate
	 * 
	 * @return Left Variate
	 */

	public double variateLeft()
	{
		return _variateLeft;
	}

	/**
	 * Return the Right Variate
	 * 
	 * @return Right Variate
	 */

	public double variateRight()
	{
		return _variateRight;
	}

	/**
	 * Return the Left Objective Function
	 * 
	 * @return Left Objective Function
	 */

	public double objectiveFunctionLeft()
	{
		return _objectiveFunctionLeft;
	}

	/**
	 * Return the Right Objective Function
	 * 
	 * @return Right Objective Function
	 */

	public double objectiveFunctionRight()
	{
		return _objectiveFunctionRight;
	}

	/**
	 * Set the Brackets in the Output Object
	 * 
	 * @param variateLeft Left Variate
	 * @param variateRight Right Variate
	 * @param objectiveFunctionLeft Left Objective Function
	 * @param objectiveFunctionRight Right Objective Function
	 * @param startingVariate Starting Variate
	 * 
	 * @return TRUE - Successfully set
	 */

	public boolean done (
		final double variateLeft,
		final double variateRight,
		final double objectiveFunctionLeft,
		final double objectiveFunctionRight,
		final double startingVariate)
	{
		if (!NumberUtil.IsValid (_variateLeft = variateLeft) ||
			!NumberUtil.IsValid (_variateRight = variateRight) ||
			!NumberUtil.IsValid (_objectiveFunctionLeft = objectiveFunctionLeft) ||
			!NumberUtil.IsValid (_objectiveFunctionRight = objectiveFunctionRight) ||
			!setStartingVariate (startingVariate))
		{
			return false;
		}

		return done();
	}

	/**
	 * Make a <i>ConvergenceOutput</i> Instance for the Open Method from the Bracketing Output
	 * 
	 * @return The <i>ConvergenceOutput</i> Instance Object
	 */

	public ConvergenceOutput makeConvergenceVariate()
	{
		ConvergenceOutput convergenceOutput = null;

		try {
			convergenceOutput = new ConvergenceOutput (this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convergenceOutput.done (startingVariate()) ? convergenceOutput : null;
	}

	/**
	 * Return a String Form of the Bracketing Output
	 * 
	 * @return String Form of the Bracketing Output
	 */

	@Override public String displayString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append (super.displayString());

		sb.append ("\n\t\tLeft Bracket: " + variateLeft());

		sb.append ("\n\t\tRight Bracket: " + variateRight());

		sb.append ("\n\t\tLeft OF: " + objectiveFunctionLeft());

		sb.append ("\n\t\tRight OF: " + objectiveFunctionRight());

		return sb.toString();
	}
}
