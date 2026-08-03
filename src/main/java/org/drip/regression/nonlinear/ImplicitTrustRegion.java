
package org.drip.regression.nonlinear;

import org.drip.numerical.common.NumberUtil;

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
 * <i>ImplicitTrustRegion</i> holds the Implicit Trust Regions used in Non-linear Least Squares Regression.
 * 	The References are:
 *  
 * <br>
 * 	<ul>
 * 		<li>
 * 			Madsen, K., H. B. Nielsen, and O. Tingleff (2004): Methods for Non-linear Least Squares Problems
 * 				https://www2.imm.dtu.dk/pubdb/edoc/imm3215.pdf
 * 		</li>
 * 		<li>
 * 			Wikipedia (2025): Trust Region https://en.wikipedia.org/wiki/Trust_region
 * 		</li>
 * 	</ul>
 *
 * <br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/README.md">Regression Engine Core and the Unit Regressors</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/README.md">Non-linear Least Squares Regression</a></li>
 *  </table>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ImplicitTrustRegion
{

	/**
	 * Lower Bound of the Prediction to Actual Function Gap Ratio
	 */

	public static final double PREDICTION_RATIO_LOWER_BOUND = 0.25;

	/**
	 * Upper Bound of the Prediction to Actual Function Gap Ratio
	 */

	public static final double PREDICTION_RATIO_UPPER_BOUND = 0.50;

	private double _lambda = Double.NaN;
	private double _expansionFactor = Double.NaN;
	private double _contractionFactor = Double.NaN;
	private double _predictionRatioLowerBound = Double.NaN;
	private double _predictionRatioUpperBound = Double.NaN;

	/**
	 * Construct an Standard Instance of <i>ImplicitTrustRegion</i>
	 * 
	 * @param expansionFactor Lambda Expansion Factor
	 * @param contractionFactor Lambda Contraction Factor
	 * @param lambda Trust Region Proxy Lambda
	 * 
	 * @return Standard Instance of <i>ImplicitTrustRegion</i>
	 */

	public static final ImplicitTrustRegion Standard (
		final double expansionFactor,
		final double contractionFactor,
		final double lambda)
	{
		try {
			return new ImplicitTrustRegion (
				PREDICTION_RATIO_LOWER_BOUND,
				PREDICTION_RATIO_UPPER_BOUND,
				expansionFactor,
				contractionFactor,
				lambda
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>ImplicitTrustRegion</i> Constructor
	 * 
	 * @param predictionRatioLowerBound Lower Bound of the Prediction Ratio
	 * @param predictionRatioUpperBound Upper Bound of the Prediction Ratio
	 * @param expansionFactor Lambda Expansion Factor
	 * @param contractionFactor Lambda Contraction Factor
	 * @param lambda Trust Region Proxy Lambda
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ImplicitTrustRegion (
		final double predictionRatioLowerBound,
		final double predictionRatioUpperBound,
		final double expansionFactor,
		final double contractionFactor,
		final double lambda)
		throws Exception
	{
		if (!NumberUtil.IsValid (_predictionRatioLowerBound = predictionRatioLowerBound) ||
				0. >= _predictionRatioLowerBound ||
			!NumberUtil.IsValid (_predictionRatioUpperBound = predictionRatioUpperBound) ||
				_predictionRatioUpperBound <= _predictionRatioLowerBound |
			!NumberUtil.IsValid (_expansionFactor = expansionFactor) || 1. >= _expansionFactor ||
			!NumberUtil.IsValid (_contractionFactor = contractionFactor) || 1. >= contractionFactor)
		{
			throw new Exception ("ImplicitTrustRegion Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Lower Bound of the Prediction Ratio
	 * 
	 * @return Lower Bound of the Prediction Ratio
	 */

	public double predictionRatioLowerBound()
	{
		return _predictionRatioLowerBound;
	}

	/**
	 * Retrieve the Upper Bound of the Prediction Ratio
	 * 
	 * @return Upper Bound of the Prediction Ratio
	 */

	public double predictionRatioUpperBound()
	{
		return _predictionRatioUpperBound;
	}

	/**
	 * Retrieve the Lambda Expansion Factor
	 * 
	 * @return Lambda Expansion Factor
	 */

	public double expansionFactor()
	{
		return _expansionFactor;
	}

	/**
	 * Retrieve the Lambda Contraction Factor
	 * 
	 * @return Lambda Contraction Factor
	 */

	public double contractionFactor()
	{
		return _contractionFactor;
	}

	/**
	 * Retrieve the Trust Region Proxy Lambda
	 * 
	 * @return Trust Region Proxy Lambda
	 */

	public double lambda()
	{
		return _lambda;
	}

	/**
	 * Update <i>Lambda</i> Based on the Predicted and the Actual Gaps in the Objective Function
	 * 
	 * @param predictedObjectiveFunctionGap Predicted Objective Function Gap
	 * @param actualObjectiveFunctionGap Actual Objective Function Gap
	 * 
	 * @return TRUE - <i>Lambda</i> successfully updated
	 */

	public boolean update (
		final double predictedObjectiveFunctionGap,
		final double actualObjectiveFunctionGap)
	{
		if (!NumberUtil.IsValid (predictedObjectiveFunctionGap) || 0. >= predictedObjectiveFunctionGap ||
			!NumberUtil.IsValid (actualObjectiveFunctionGap) || 0. >= actualObjectiveFunctionGap)
		{
			return false;
		}

		double predictionRatio = predictedObjectiveFunctionGap / actualObjectiveFunctionGap;

		if (predictionRatio < _predictionRatioLowerBound) {
			_lambda *= _expansionFactor;
		} else if (predictionRatio > _predictionRatioUpperBound) {
			_lambda /= _contractionFactor;
		}

		return true;
	}
}
