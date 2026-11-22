
package org.drip.state.eostree;

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
 * <i>VertexElastic</i> holds the Path-dependent Vertex Properties of a Single Slice Vertex with Pillar
 *  Segments built according to the Kalotay, Williams, and Fabozzi (1993) Tree-based Model for valuing bonds
 *  with Embedded Options. The References are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/eostree/README.md">EOS Bond Tree Based Valuation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class VertexElastic
{
	private VertexInelastic _inelastic = null;
	private double[] _preExerciseDirtyPVArray = null;
	private double[] _postExerciseDirtyPVArray = null;

	/**
	 * Retrieve the Terminal <i>VertexElastic</i> Instance
	 * 
	 * @param inelastic Underlying <i>VertexInelastic</i> Instance
	 * @param nodeCount Node Count
	 * 
	 * @return Terminal <i>VertexElastic</i> Instance
	 */

	public static final VertexElastic Terminal (
		final VertexInelastic inelastic,
		final int nodeCount)
	{
		if (null == inelastic || 0 >= nodeCount) {
			return null;
		}

		double[] pvArray = new double[nodeCount];

		double terminalPayAmount = inelastic.payAmount();

		for (int nodeIndex = 0; nodeIndex < nodeCount; ++nodeIndex) {
			pvArray[nodeIndex] = terminalPayAmount;
		}

		try {
			return new VertexElastic (inelastic, pvArray, pvArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>VertexElastic</i> Constructor
	 * 
	 * @param inelastic Underlying <i>VertexInelastic</i> Instance
	 * @param postExerciseDirtyPVArray Vertex Post-exercise Dirty PV Array
	 * @param preExerciseDirtyPVArray Vertex Post-exercise Dirty PV Array
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public VertexElastic (
		final VertexInelastic inelastic,
		final double[] postExerciseDirtyPVArray,
		final double[] preExerciseDirtyPVArray)
		throws Exception
	{
		if (null == (_inelastic = inelastic) ||
			null == (_postExerciseDirtyPVArray = postExerciseDirtyPVArray) ||
				0 == _postExerciseDirtyPVArray.length || !NumberUtil.IsValid (_postExerciseDirtyPVArray) ||
			null == (_preExerciseDirtyPVArray = preExerciseDirtyPVArray) ||
				0 == _preExerciseDirtyPVArray.length || !NumberUtil.IsValid (_preExerciseDirtyPVArray))
		{
			throw new Exception ("VertexElastic Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Underlying <i>VertexInelastic</i> Instance
	 * 
	 * @return Underlying <i>VertexInelastic</i> Instance
	 */

	public VertexInelastic inelastic()
	{
		return _inelastic;
	}

	/**
	 * Retrieve the Vertex Post-exercise Dirty PV Array
	 * 
	 * @return Vertex Post-exercise Dirty PV Array
	 */

	public double[] postExerciseDirtyPVArray()
	{
		return _postExerciseDirtyPVArray;
	}

	/**
	 * Retrieve the Vertex Pre-exercise Dirty PV Array
	 * 
	 * @return Vertex Pre-exercise Dirty PV Array
	 */

	public double[] preExerciseDirtyPVArray()
	{
		return _preExerciseDirtyPVArray;
	}

	/**
	 * Retrieve the Vertex Post-exercise Clean PV Array
	 * 
	 * @return Vertex Post-exercise Clean PV Array
	 */

	public double[] postExerciseCleanPVArray()
	{
		double[] postExerciseCleanPVArray = new double[_postExerciseDirtyPVArray.length];

		double accruedAmount = _inelastic.accrual().amount();

		for (int i = 0; i < _postExerciseDirtyPVArray.length; ++i) {
			postExerciseCleanPVArray[i] = _postExerciseDirtyPVArray[i] - accruedAmount;
		}

		return postExerciseCleanPVArray;
	}

	/**
	 * Retrieve the Vertex Pre-exercise Clean PV Array
	 * 
	 * @return Vertex Pre-exercise Clean PV Array
	 */

	public double[] preExerciseCleanPVArray()
	{
		double[] preExerciseCleanPVArray = new double[_preExerciseDirtyPVArray.length];

		double accruedAmount = _inelastic.accrual().amount();

		for (int i = 0; i < _postExerciseDirtyPVArray.length; ++i) {
			preExerciseCleanPVArray[i] = _preExerciseDirtyPVArray[i] - accruedAmount;
		}

		return preExerciseCleanPVArray;
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
		return prefix + " (Inelastic: " + _inelastic.toString() +
			"; Post-exercise Dirty PV Array: " +
				NumberUtil.ArrayRow (_postExerciseDirtyPVArray, 1, 4, false) +
				"; Pre-exercise Dirty PV Array: " +
				NumberUtil.ArrayRow (_preExerciseDirtyPVArray, 1, 4, false) + ")";
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
