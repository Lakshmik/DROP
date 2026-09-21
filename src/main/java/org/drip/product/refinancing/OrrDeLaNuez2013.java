
package org.drip.product.refinancing;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.measure.dynamics.OrnsteinUhlenbeckDriftWander;
import org.drip.product.muni.DeGuillaumeRebonatoPogudin;
import org.drip.product.muni.DeGuillaumeRebonatoPogudinMarketSettings;
import org.drip.product.muni.RefinancingEnsemble;
import org.drip.product.muni.RefinancingPathEntry;
import org.drip.product.muni.RefinancingPathGenerator;

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
 * <i>OrrDeLaNuez2013</i> performs a Monte-Carlo Evolution of the Market Yield Term Structure for Muni
 *  Sub-markets, i.e., Tax-exempt, Taxable, and Escrow Yield Curves using the de Guillaume, Rebonato, and
 *  Pogudin (2013) Scheme. Default Evolution is using Ornstein-Uhlenbeck Evolver. The generated Ensemble is
 *  used for Valuing Bond Refund/Call Features. The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Ang, A., R. C. Green, and Y. Xing (2013): <i>Advance Re-fundings of Municipal Bonds</i>
 *  			https://www.nber.org/papers/w19459
 *  	</li>
 *  	<li>
 *  		de Guillaume, N., R. Rebonato, and A. Pogudin (2013): The Nature of the Dependence of the
 *  			Magnitude of the Rates Moves on the Rates Levels: A Universal Relationship <i>Quantitative
 *  			Finance</i> <b>13 (3)</b> 351-367
 *  	</li>
 *  	<li>
 *  		Orr, P., and D. de la Nuez (2013): <i>The Right and Wrong Models for Evaluating Callable
 *  			Municipal Bonds</i> <b>eSSRN</b>
 *  	</li>
 *  	<li>
 *  		Rebonato, R. (2003): <i>Term Structure Models: A Review</i>
 *  			https://dept.math.lsa.umich.edu/~conlon/math623/rebonato_review.pdf
 *  	</li>
 *  	<li>
 *  		Rebonato, R., and S. K. Nawalkha (2011): <i>What Interest Rate Models to Use? Buy Side versus
 *  			Sell Side</i> <b>eSSRN</b>
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/README.md">Product Components/Baskets for Credit, FRA, FX, Govvie, Rates, and Option Asset Classes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/refinancing/README.md">Evaluation of Product Re-financing PnL</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class OrrDeLaNuez2013
{
	private static final boolean _Blog = false;

	private int _pathCount = Integer.MIN_VALUE;
	private double[][] _yieldCorrelationMatrix = null;
	private OrnsteinUhlenbeckDriftWander _escrowOrnsteinUhlenbeckDriftWander = null;
	private OrnsteinUhlenbeckDriftWander _taxableOrnsteinUhlenbeckDriftWander = null;
	private OrnsteinUhlenbeckDriftWander _taxExemptOrnsteinUhlenbeckDriftWander = null;

	/**
	 * Construct a Standard Instance of <i>OrrDeLaNuez2013</i>
	 * 
	 * @param pathCount Number of Simulation Paths
	 * @param ornsteinUhlenbeckDriftWander <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * @param yieldCorrelationMatrix Matrix of Yield Correlation Weiners
	 * 
	 * @return Standard Instance of <i>OrrDeLaNuez2013</i>
	 */

	public static final OrrDeLaNuez2013 Standard (
		final int pathCount,
		final OrnsteinUhlenbeckDriftWander ornsteinUhlenbeckDriftWander,
		final double[][] yieldCorrelationMatrix)
	{
		try {
			return new OrrDeLaNuez2013 (
				pathCount,
				ornsteinUhlenbeckDriftWander,
				ornsteinUhlenbeckDriftWander,
				ornsteinUhlenbeckDriftWander,
				yieldCorrelationMatrix
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>OrrDeLaNuez2013</i> Constructor
	 * 
	 * @param pathCount Number of Simulation Paths
	 * @param taxExemptOrnsteinUhlenbeckDriftWander Tax-exempt Yield <i>OrnsteinUhlenbeckDriftWander</i>
	 * 												Instance
	 * @param taxableOrnsteinUhlenbeckDriftWander Taxable Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * @param escrowOrnsteinUhlenbeckDriftWander Escrow Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * @param yieldCorrelationMatrix Matrix of Yield Correlation Weiners
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public OrrDeLaNuez2013 (
		final int pathCount,
		final OrnsteinUhlenbeckDriftWander taxExemptOrnsteinUhlenbeckDriftWander,
		final OrnsteinUhlenbeckDriftWander taxableOrnsteinUhlenbeckDriftWander,
		final OrnsteinUhlenbeckDriftWander escrowOrnsteinUhlenbeckDriftWander,
		final double[][] yieldCorrelationMatrix)
		throws Exception
	{
		if (0 >= (_pathCount = pathCount) ||
			null == (_taxExemptOrnsteinUhlenbeckDriftWander = taxExemptOrnsteinUhlenbeckDriftWander) ||
			null == (_taxableOrnsteinUhlenbeckDriftWander = taxableOrnsteinUhlenbeckDriftWander) ||
			null == (_escrowOrnsteinUhlenbeckDriftWander = escrowOrnsteinUhlenbeckDriftWander))
		{
			throw new Exception ("OrrDeLaNuez2013 Constructor => Invalid Inputs");
		}

		_yieldCorrelationMatrix = yieldCorrelationMatrix;
	}

	/**
	 * Retrieve the Number of Simulation Paths
	 * 
	 * @return Number of Simulation Paths
	 */

	public int pathCount()
	{
		return _pathCount;
	}

	/**
	 * Retrieve the Tax-exempt Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * 
	 * @return Tax-exempt Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 */

	public OrnsteinUhlenbeckDriftWander taxExemptOrnsteinUhlenbeckDriftWander()
	{
		return _taxExemptOrnsteinUhlenbeckDriftWander;
	}

	/**
	 * Retrieve the Taxable Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * 
	 * @return Taxable Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 */

	public OrnsteinUhlenbeckDriftWander taxableOrnsteinUhlenbeckDriftWander()
	{
		return _taxableOrnsteinUhlenbeckDriftWander;
	}

	/**
	 * Retrieve the Escrow Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * 
	 * @return Escrow Yield <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 */

	public OrnsteinUhlenbeckDriftWander escrowOrnsteinUhlenbeckDriftWander()
	{
		return _escrowOrnsteinUhlenbeckDriftWander;
	}

	/**
	 * Retrieve the Matrix of Yield Correlation Weiners
	 * 
	 * @return Matrix of Yield Correlation Weiners
	 */

	public double[][] yieldCorrelationMatrix()
	{
		return _yieldCorrelationMatrix;
	}

	/**
	 * Generate the <i>RefinancingEnsemble</i> Instance for the Specified Bond and Market Inputs
	 * 
	 * @param deGuillaumeRebonatoPogudinMarketYield <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 * @param refinancingPathGenerator Bond <i>RefinancingPathGenerator</i> Instance
	 * @param simulationDateList List of Simulation Dates
	 * 
	 * @return <i>RefinancingEnsemble</i> Instance for the Specified Bond and Market Inputs
	 */

	public RefinancingEnsemble refinancingEnsemble (
		final DeGuillaumeRebonatoPogudinMarketSettings deGuillaumeRebonatoPogudinMarketYield,
		final RefinancingPathGenerator refinancingPathGenerator,
		final List<JulianDate> simulationDateList)
	{
		if (null == refinancingPathGenerator) {
			return null;
		}

		DeGuillaumeRebonatoPogudin deGuillaumeRebonatoPogudin = DeGuillaumeRebonatoPogudin.Standard (
			_taxExemptOrnsteinUhlenbeckDriftWander,
			_taxableOrnsteinUhlenbeckDriftWander,
			_escrowOrnsteinUhlenbeckDriftWander,
			deGuillaumeRebonatoPogudinMarketYield,
			_yieldCorrelationMatrix
		);

		if (null == deGuillaumeRebonatoPogudin) {
			return null;
		}

		RefinancingEnsemble refinancingEnsemble = new RefinancingEnsemble();

		for (int pathIndex = 0; pathIndex < _pathCount; ++pathIndex) {
			if (_Blog) {
				System.out.println ("\t|| Simulation Path #: " + pathIndex);
			}

			TreeMap<JulianDate, RefinancingPathEntry> refinancingPathPnLEntryMap =
				refinancingPathGenerator.generate (
					deGuillaumeRebonatoPogudin.evolve (
						deGuillaumeRebonatoPogudinMarketYield,
						simulationDateList
					)
				);

			if (null == refinancingPathPnLEntryMap) {
				return null;
			}

			for (JulianDate date : refinancingPathPnLEntryMap.keySet()) {
				refinancingEnsemble.add (date, refinancingPathPnLEntryMap.get (date));
			}
		}

		return refinancingEnsemble.updateCentralMeasures() ? refinancingEnsemble : null;
	}

	/**
	 * Generate the <i>AROEnsemble</i> Instance for the Specified Bond and Market Inputs
	 * 
	 * @param deGuillaumeRebonatoPogudinMarketYield <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 * @param aroPathGenerator Bond <i>AROPathGenerator</i> Instance
	 * @param taxExempt TRUE - Apply Tax-exempt Re-financing
	 * 
	 * @return <i>AROEnsemble</i> Instance for the Specified Bond and Market Inputs
	 */

	public AROEnsemble aroEnsemble (
		final DeGuillaumeRebonatoPogudinMarketSettings deGuillaumeRebonatoPogudinMarketYield,
		final AROPathGenerator aroPathGenerator,
		final boolean taxExempt)
	{
		if (null == aroPathGenerator) {
			return null;
		}

		AROSetting aroSetting = aroPathGenerator.setting();

		List<JulianDate> simulationDateList = new ArrayList<JulianDate>();

		simulationDateList.add (aroSetting.embeddedOptionExerciseDate());

		simulationDateList.add (aroSetting.refinancingDate());

		DeGuillaumeRebonatoPogudin deGuillaumeRebonatoPogudin = DeGuillaumeRebonatoPogudin.Standard (
			_taxExemptOrnsteinUhlenbeckDriftWander,
			_taxableOrnsteinUhlenbeckDriftWander,
			_escrowOrnsteinUhlenbeckDriftWander,
			deGuillaumeRebonatoPogudinMarketYield,
			_yieldCorrelationMatrix
		);

		if (null == deGuillaumeRebonatoPogudin) {
			return null;
		}

		AROEnsemble aroEnsemble = new AROEnsemble();

		for (int pathIndex = 0; pathIndex < _pathCount; ++pathIndex) {
			if (_Blog) {
				System.out.println ("\t|| Simulation Path #: " + pathIndex);
			}

			if (!aroEnsemble.add (
				aroPathGenerator.generate (
					deGuillaumeRebonatoPogudin.evolve (
						deGuillaumeRebonatoPogudinMarketYield,
						simulationDateList
					),
					taxExempt
				)
			))
			{
				return null;
			}
		}

		return aroEnsemble.updateCentralMeasures() ? aroEnsemble : null;
	}
}
