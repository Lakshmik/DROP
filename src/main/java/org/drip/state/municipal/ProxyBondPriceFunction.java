
package org.drip.state.municipal;

import java.util.List;
import java.util.TreeMap;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1solver.FixedPointFinderBracketing;
import org.drip.function.r1tor1solver.FixedPointFinderBrent;
import org.drip.function.r1tor1solver.FixedPointFinderOutput;
import org.drip.function.r1tor1solver.VariateIteratorPrimitive;
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
 * <i>ProxyBondPriceFunction</i> calibrates the specified Base Procy Bond Yield used in the Kalotay,
 * 	Williams, and Fabozzi (1993) Tree-based Model for valuing bonds with Embedded Options. The References
 * 	are:
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/municipal/README.md">Municipal Latent State Curve Estimator</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ProxyBondPriceFunction
{
	private ProxyBond _underlying = null;
	private double _annualizedForwardYieldVolatility = Double.NaN;
	private KalotayWilliamsFabozzi _kalotayWilliamsFabozzi = null;
	private TreeMap<Double, Double> _timeToProjectedBaseForwardYieldMap = null;

	private double priceFromBaseForwardYield (
		final double calibrationEndTime,
		final double projectedBaseForwardYield)
		throws Exception
	{
		if (null == _timeToProjectedBaseForwardYieldMap) {
			_timeToProjectedBaseForwardYieldMap = new TreeMap<Double, Double>();
		}

		_timeToProjectedBaseForwardYieldMap.put (calibrationEndTime, projectedBaseForwardYield);

		if (!_kalotayWilliamsFabozzi.applyProjectedBaseForwardYield (
			_annualizedForwardYieldVolatility,
			_timeToProjectedBaseForwardYieldMap
		))
		{
			throw new Exception ("ProxyBondPriceFunction::priceFromBaseForwardYield => Invalid Inputs");
		}

		TreeMap<Double, List<Double>> projectedBondValueListMap = _underlying.valueTree (
			_kalotayWilliamsFabozzi
		);

		if (null == projectedBondValueListMap) {
			throw new Exception ("ProxyBondPriceFunction::priceFromBaseForwardYield => Invalid Inputs");
		}

		return projectedBondValueListMap.firstEntry().getValue().get (0);
	}

	/**
	 * <i>ProxyBondPriceFunction</i> Constructor
	 * 
	 * @param kalotayWilliamsFabozzi <i>KalotayWilliamsFabozzi</i> Model Implementation
	 * @param annualizedForwardYieldVolatility Annualized Forward Yield Volatility
	 * @param timeToProjectedBaseForwardYieldMap Projected Base Forward Yield Time Map
	 * @param underlying Underlying Calibration Proxy Bond
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ProxyBondPriceFunction (
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi,
		final double annualizedForwardYieldVolatility,
		final TreeMap<Double, Double> timeToProjectedBaseForwardYieldMap,
		final ProxyBond underlying)
		throws Exception
	{
		if (null == (_kalotayWilliamsFabozzi = kalotayWilliamsFabozzi) ||
			!NumberUtil.IsValid (_annualizedForwardYieldVolatility = annualizedForwardYieldVolatility) ||
				0. > _annualizedForwardYieldVolatility ||
			null == (_underlying = underlying))
		{
			throw new Exception ("ProxyBondPriceFunction Constructor => Invalid Inputs");
		}

		_timeToProjectedBaseForwardYieldMap = timeToProjectedBaseForwardYieldMap;
	}

	/**
	 * Retrieve the Underlying <i>KalotayWilliamsFabozzi</i> Model Implementation
	 * 
	 * @return Underlying <i>KalotayWilliamsFabozzi</i> Model Implementation
	 */

	public KalotayWilliamsFabozzi kalotayWilliamsFabozzi()
	{
		return _kalotayWilliamsFabozzi;
	}

	/**
	 * Retrieve the Annualized Forward Yield Volatility
	 * 
	 * @return Annualized Forward Yield Volatility
	 */

	public double annualizedForwardYieldVolatility()
	{
		return _annualizedForwardYieldVolatility;
	}

	/**
	 * Retrieve the Projected Base Forward Yield Time Map
	 * 
	 * @return Projected Base Forward Yield Time Map
	 */

	public TreeMap<Double, Double> timeToProjectedBaseForwardYieldMap()
	{
		return _timeToProjectedBaseForwardYieldMap;
	}

	/**
	 * Retrieve the Underlying Calibration Proxy Bond
	 * 
	 * @return Underlying Calibration Proxy Bond
	 */

	public ProxyBond underlying()
	{
		return _underlying;
	}

	/**
	 * Evaluate the Price for the given Yield Basis
	 * 
	 * @param yieldBasis Yield Basis
	 *  
	 * @return Returns the calculated Price
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public double priceFromYieldBasis (
		final double yieldBasis)
		throws Exception
	{
		if (!_kalotayWilliamsFabozzi.applyBasisYield (yieldBasis)) {
			throw new Exception ("ProxyBondPriceFunction::priceFromYieldBasis => Invalid Inputs");
		}

		if (!_kalotayWilliamsFabozzi.applyProjectedBaseForwardYield (
			_annualizedForwardYieldVolatility,
			KalotayWilliamsFabozzi.BASE_FORWARD_NODE_ADJUSTED_CUMULATIVE_YIELD
		))
		{
			throw new Exception ("ProxyBondPriceFunction::priceFromYieldBasis => Invalid Inputs");
		}

		TreeMap<Double, List<Double>> projectedBondValueListMap = _underlying.valueTree (
			_kalotayWilliamsFabozzi
		);

		if (null == projectedBondValueListMap) {
			throw new Exception ("ProxyBondPriceFunction::priceFromYieldBasis => Invalid Inputs");
		}

		return projectedBondValueListMap.firstEntry().getValue().get (0);
	}

	/**
	 * Retrieve the Base Yield given the Price
	 * 
	 * @param calibrationEndTime The Calibration End Time
	 * @param price Price
	 * 
	 * @return Base Yield
	 * 
	 * @throws Exception Thrown if the Base Yield cannot be Calibrated
	 */

	public double baseYieldForPrice (
		final double calibrationEndTime,
		final double price)
		throws Exception
	{
		if (!NumberUtil.IsValid (calibrationEndTime) || 0. >= calibrationEndTime ||
			_underlying.maturityTime() < calibrationEndTime)
		{
			throw new Exception ("ProxyBondPriceFunction::baseYieldForPrice => Cannot Find Root");
		}

		return new FixedPointFinderBrent (
			price,
			new R1ToR1 (null)
			{
				@Override public double evaluate (
					final double projectedBaseForwardYield)
					throws Exception
				{
					return priceFromBaseForwardYield (calibrationEndTime, projectedBaseForwardYield);
				}
			},
			true
		).findRoot().root();
	}

	/**
	 * Retrieve the Yield Basis given the Price
	 * 
	 * @param price Price
	 * 
	 * @return Yield Basis
	 * 
	 * @throws Exception Thrown if the Base Yield cannot be Calibrated
	 */

	public double yieldBasisForPrice (
		final double price)
		throws Exception
	{
		FixedPointFinderOutput fixedPointFinderOutput = new FixedPointFinderBracketing (
			price,
			new R1ToR1 (null)
			{
				@Override public double evaluate (
					final double yieldBasis)
					throws Exception
				{
					return priceFromYieldBasis (yieldBasis);
				}
			},
			null,
			VariateIteratorPrimitive.BISECTION,
			true
		).findRoot();

		if (null == fixedPointFinderOutput ||
			!_kalotayWilliamsFabozzi.removeBasisYield() ||
			!_kalotayWilliamsFabozzi.applyProjectedBaseForwardYield (
				_annualizedForwardYieldVolatility,
				KalotayWilliamsFabozzi.BASE_FORWARD_NODE_ADJUSTED_CUMULATIVE_YIELD
			)
		)
		{
			throw new Exception ("ProxyBondPriceFunction::yieldBasisForPrice => Cannot Find Root");
		}

		return fixedPointFinderOutput.root();
	}
}
