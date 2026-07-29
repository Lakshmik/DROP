
package org.drip.optimization.neldermead;

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
 * <i>DownhillSimplexIterationDiagnostics</i> maintains the Diagnostics associated with a Single Iteration
 * 	inside the Downhill Simplex Scheme. The References are:
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

public class DownhillSimplexIterationDiagnostics
{
	private double[] _centroidVertex = null;
	private DownhillSimplexVertexes _vertexes = null;
	private ObjectiveFunctionCoordinate _lowestObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _highestObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _expandedObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _reflectedObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _contractedObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _shrinkCentroidObjectiveFunctionCoordinate = null;
	private ObjectiveFunctionCoordinate _penultimateHighestObjectiveFunctionCoordinate = null;

	protected DownhillSimplexIterationDiagnostics()
	{
	}

	/**
	 * Set the Centroid Vertex
	 * 
	 * @param centroidVertex Centroid Vertex
	 * 
	 * @return TRUE - Centroid Vertex successfully set
	 */

	public boolean setCentroidVertex (
		final double[] centroidVertex)
	{
		if (null == centroidVertex) {
			return false;
		}

		_centroidVertex = centroidVertex;
		return true;
	}

	/**
	 * Retrieve the Centroid Vertex
	 * 
	 * @return Centroid Vertex
	 */

	public double[] centroidVertex()
	{
		return _centroidVertex;
	}

	/**
	 * Set the Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param highestObjectiveFunctionCoordinate Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Highest <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setHighestObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate highestObjectiveFunctionCoordinate)
	{
		if (null == highestObjectiveFunctionCoordinate) {
			return false;
		}

		_highestObjectiveFunctionCoordinate = highestObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Highest <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate highestObjectiveFunctionCoordinate()
	{
		return _highestObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Reflected <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param reflectedObjectiveFunctionCoordinate Reflected <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Reflected <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setReflectedObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate reflectedObjectiveFunctionCoordinate)
	{
		if (null == reflectedObjectiveFunctionCoordinate) {
			return false;
		}

		_reflectedObjectiveFunctionCoordinate = reflectedObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Reflected <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Reflected <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate reflectedObjectiveFunctionCoordinate()
	{
		return _reflectedObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Expanded <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param expandedObjectiveFunctionCoordinate Expanded <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Expanded <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setExpandedObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate expandedObjectiveFunctionCoordinate)
	{
		if (null == expandedObjectiveFunctionCoordinate) {
			return false;
		}

		_expandedObjectiveFunctionCoordinate = expandedObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Expanded <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Expanded <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate expandedObjectiveFunctionCoordinate()
	{
		return _expandedObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Penultimate Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param penultimateHighestObjectiveFunctionCoordinate
	 * 	Penultimate Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Penultimate Highest <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setPenultimateHighestObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate penultimateHighestObjectiveFunctionCoordinate)
	{
		if (null == penultimateHighestObjectiveFunctionCoordinate) {
			return false;
		}

		_penultimateHighestObjectiveFunctionCoordinate = penultimateHighestObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Penultimate Highest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Penultimate Highest <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate penultimateHighestObjectiveFunctionCoordinate()
	{
		return _penultimateHighestObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Lowest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param lowestObjectiveFunctionCoordinate Lowest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Lowest <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setLowestObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate lowestObjectiveFunctionCoordinate)
	{
		if (null == lowestObjectiveFunctionCoordinate) {
			return false;
		}

		_lowestObjectiveFunctionCoordinate = lowestObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Lowest <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Lowest <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate lowestObjectiveFunctionCoordinate()
	{
		return _lowestObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Contracted <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param contractedObjectiveFunctionCoordinate Contracted <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Contracted <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setContractedObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate contractedObjectiveFunctionCoordinate)
	{
		if (null == contractedObjectiveFunctionCoordinate) {
			return false;
		}

		_contractedObjectiveFunctionCoordinate = contractedObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Contracted <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Contracted <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate contractedObjectiveFunctionCoordinate()
	{
		return _contractedObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Shrink Centroid <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @param shrinkCentroidObjectiveFunctionCoordinate Shrink Centroid <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return TRUE - Shrink Centroid <i>ObjectiveFunctionCoordinate</i> successfully set
	 */

	public boolean setShrinkCentroidObjectiveFunctionCoordinate (
		final ObjectiveFunctionCoordinate shrinkCentroidObjectiveFunctionCoordinate)
	{
		if (null == shrinkCentroidObjectiveFunctionCoordinate) {
			return false;
		}

		_shrinkCentroidObjectiveFunctionCoordinate = shrinkCentroidObjectiveFunctionCoordinate;
		return true;
	}

	/**
	 * Retrieve Shrink Centroid <i>ObjectiveFunctionCoordinate</i>
	 * 
	 * @return Shrink Centroid <i>ObjectiveFunctionCoordinate</i>
	 */

	public ObjectiveFunctionCoordinate shrinkCentroidObjectiveFunctionCoordinate()
	{
		return _shrinkCentroidObjectiveFunctionCoordinate;
	}

	/**
	 * Set the Downhill Simplex Vertexes Instance
	 * 
	 * @param vertexes Downhill Simplex Vertexes Instance
	 * 
	 * @return TRUE - The Downhill Simplex Vertexes Instance successfully set
	 */

	public boolean setVertexes (
		final DownhillSimplexVertexes vertexes)
	{
		if (null == vertexes) {
			return false;
		}

		_vertexes = vertexes;
		return true;
	}

	/**
	 * Retrieve Downhill Simplex Vertexes Instance
	 * 
	 * @return Downhill Simplex Vertexes Instance
	 */

	public DownhillSimplexVertexes vertexes()
	{
		return _vertexes;
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
		String dump = prefix + "{";

		if (null != _centroidVertex) {
			dump += "(" + NumberUtil.ArrayRow (_centroidVertex, 1, 4, false) + "); ";
		}

		if (null != _highestObjectiveFunctionCoordinate) {
			dump += "(" + _highestObjectiveFunctionCoordinate + "); ";
		}

		if (null != _reflectedObjectiveFunctionCoordinate) {
			dump += "(" + _reflectedObjectiveFunctionCoordinate + "); ";
		}

		if (null != _expandedObjectiveFunctionCoordinate) {
			dump += "(" + _expandedObjectiveFunctionCoordinate + "); ";
		}

		if (null != _penultimateHighestObjectiveFunctionCoordinate) {
			dump += "(" + _penultimateHighestObjectiveFunctionCoordinate + "); ";
		}

		if (null != _lowestObjectiveFunctionCoordinate) {
			dump += "(" + _lowestObjectiveFunctionCoordinate + "); ";
		}

		if (null != _contractedObjectiveFunctionCoordinate) {
			dump += "(" + _contractedObjectiveFunctionCoordinate + "); ";
		}

		if (null != _shrinkCentroidObjectiveFunctionCoordinate) {
			dump += "(" + _shrinkCentroidObjectiveFunctionCoordinate + "); ";
		}

		if (null != _vertexes) {
			dump += "(" + _vertexes + "); ";
		}

		return dump + "}";
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
