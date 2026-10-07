
package org.drip.state.govvie;

import java.util.ArrayList;
import java.util.List;

import org.drip.numerical.common.NumberUtil;
import org.drip.optimization.neldermead.DownhillSimplex;
import org.drip.sequence.random.BoundedUniform;

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
 * <i>MultiBondCurveCalibrator</i> implements the Multi-bond Least-squares Govvie Curve Calibration using a
 * 	Set of Bond Quotes through the Nelder-Mead Scheme. The References are:
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/state/govvie/README.md">Govvie Latent State Curve Estimator</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MultiBondCurveCalibrator
{
	private boolean _diagnosticsOn = false;
	private boolean _incorporateCentroid = false;
	private int _vertexCount = Integer.MIN_VALUE;
	private double _lowerSearchBound = Double.NaN;
	private double _upperSearchBound = Double.NaN;
	private MultiBondLeastSquaresFunction _multiBondLeastSquaresFunction = null;

	private List<double[]> vertexList()
	{
		BoundedUniform boundedUniform = null;

		List<double[]> vertexList = new ArrayList<double[]>();

		int variateDimension = _multiBondLeastSquaresFunction.dimension();

		try {
			boundedUniform = new BoundedUniform (_lowerSearchBound, _upperSearchBound);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int vertexIndex = 0; vertexIndex < _vertexCount; ++vertexIndex) {
			double[] vertex = new double[variateDimension];

			for (int variateIndex = 0; variateIndex < variateDimension; ++variateIndex) {
				vertex[variateIndex] = boundedUniform.random();
			}

			vertexList.add (vertex);
		}

		return vertexList;
	}

	/**
	 * <i>MultiBondCurveCalibrator</i> Constructor
	 * 
	 * @param multiBondLeastSquaresFunction Multi-Bond Least Squares Error Function
	 * @param vertexCount Number of Vertexes in the Simplex
	 * @param lowerSearchBound Lower Search Bound
	 * @param upperSearchBound Upper Search Bound
	 * @param incorporateCentroid TRUE - Centroid is a Candidate for the Vertex List
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public MultiBondCurveCalibrator (
		final MultiBondLeastSquaresFunction multiBondLeastSquaresFunction,
		final int vertexCount,
		final double lowerSearchBound,
		final double upperSearchBound,
		final boolean incorporateCentroid,
		final boolean diagnosticsOn)
		throws Exception
	{
		if (null == (_multiBondLeastSquaresFunction = multiBondLeastSquaresFunction) ||
			!NumberUtil.IsValid (_lowerSearchBound = lowerSearchBound) ||
			!NumberUtil.IsValid (_upperSearchBound = upperSearchBound) ||
				_upperSearchBound <= _lowerSearchBound)
		{
			throw new Exception ("MultiBondCurveCalibrator Constructor => Invalid Inputs");
		}

		int dimension = _multiBondLeastSquaresFunction.dimension();

		if ((_vertexCount = vertexCount) < dimension || Math.log (_vertexCount) > Math.log (2.) * dimension)
		{
			throw new Exception ("MultiBondCurveCalibrator Constructor => Invalid Inputs");
		}

		_incorporateCentroid = incorporateCentroid;
		_diagnosticsOn = diagnosticsOn;
	}

	/**
	 * Retrieve the Multi-Bond Least Squares Error Function
	 * 
	 * @return Multi-Bond Least Squares Error Function
	 */

	public MultiBondLeastSquaresFunction multiBondLeastSquaresFunction()
	{
		return _multiBondLeastSquaresFunction;
	}

	/**
	 * Retrieve the Number of Vertexes in the Simplex
	 * 
	 * @return Number of Vertexes in the Simplex
	 */

	public int vertexCount()
	{
		return _vertexCount;
	}

	/**
	 * Retrieve the Lower Search Bound
	 * 
	 * @return Lower Search Bound
	 */

	public double lowerSearchBound()
	{
		return _lowerSearchBound;
	}

	/**
	 * Retrieve the Upper Search Bound
	 * 
	 * @return Upper Search Bound
	 */

	public double upperSearchBound()
	{
		return _upperSearchBound;
	}

	/**
	 * Indicate if Centroid is a Candidate for the Vertex List
	 * 
	 * @return TRUE - Centroid is a Candidate for the Vertex List
	 */

	public boolean incorporateCentroid()
	{
		return _incorporateCentroid;
	}

	/**
	 * Indicate if Diagnostics has been Turned On
	 * 
	 * @return TRUE - Diagnostics has been Turned On
	 */

	public boolean diagnosticsOn()
	{
		return _diagnosticsOn;
	}

	/**
	 * Calibrate and Generate an Instance of <i>MultiBondCurveCalibrationRun</i>
	 * 
	 * @return Instance of <i>MultiBondCurveCalibrationRun</i>
	 */

	public MultiBondCurveCalibrationRun calibrate()
	{
		DownhillSimplex downhillSimplex = DownhillSimplex.Standard (
			_multiBondLeastSquaresFunction,
			vertexList(),
			_incorporateCentroid,
			_diagnosticsOn
		);

		return null == downhillSimplex ? null : MultiBondCurveCalibrationRun.Standard (
			downhillSimplex.controlRun(),
			_multiBondLeastSquaresFunction.epochDate().julian(),
			_multiBondLeastSquaresFunction.code(),
			_multiBondLeastSquaresFunction.currency(),
			_multiBondLeastSquaresFunction.calibrationDateArray()
		);
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
		return prefix + "Multi-Bond Curve Calibrator => " + _multiBondLeastSquaresFunction + prefix +
			" {Vertex Count => " + _vertexCount + " | Lower Search Bound => " + _lowerSearchBound +
			" | Upper Search Bound => " + _upperSearchBound + " | Incorporate Centroid => " +
			_incorporateCentroid + "}";
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
