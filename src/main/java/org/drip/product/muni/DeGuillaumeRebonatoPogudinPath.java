
package org.drip.product.muni;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.numerical.common.NumberUtil;
import org.drip.state.creator.ScenarioGovvieCurveBuilder;
import org.drip.state.govvie.GovvieCurve;

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
 * <i>DeGuillaumeRebonatoPogudinPath</i> holds the Single Path Simulated Market Yield Term Structure for Muni
 *  Sub-markets, i.e., Tax-exempt, Taxable, and Escrow Yield Curves using Ornstein-Uhlenbeck augmented de
 *  Guillaume, Rebonato, and Pogudin (2013) Scheme. The References are:
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/muni/README.md">Refunding and Optimal Exercise Mechanics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class DeGuillaumeRebonatoPogudinPath
{

	class RealizedQuotes
	{
		int[] _maturityDateArray = null;
		double[] _maturityYieldArray = null;
	}

	private TreeMap<JulianDate, GovvieCurve> _dateEscrowGovvieCurveMap = null;
	private TreeMap<JulianDate, GovvieCurve> _dateTaxableGovvieCurveMap = null;
	private TreeMap<JulianDate, GovvieCurve> _dateTaxExemptGovvieCurveMap = null;
	private TreeMap<JulianDate, Map<String, Double>> _escrowDateTenorYieldRealizationMap = null;
	private TreeMap<JulianDate, Map<String, Double>> _taxableDateTenorYieldRealizationMap = null;
	private TreeMap<JulianDate, Map<String, Double>> _taxExemptDateTenorYieldRealizationMap = null;

	private RealizedQuotes realizedQuotes (
		final JulianDate asOfDate,
		final Map<String, Double> tenorYieldMap)
	{
		Set<String> tenorKeySet = tenorKeySet();

		if (null == tenorKeySet) {
			return null;
		}

		TreeMap<JulianDate, String> maturityDateMap = new TreeMap<JulianDate, String>();

		RealizedQuotes realizedQuotes = new RealizedQuotes();

		int tenorCount = tenorKeySet.size();

		int tenorIndex = 0;
		realizedQuotes._maturityDateArray = new int[tenorCount];
		realizedQuotes._maturityYieldArray = new double[tenorCount];

		for (String tenor : tenorKeySet) {
			maturityDateMap.put (asOfDate.addTenor (tenor), tenor);
		}

		for (JulianDate maturityDate : maturityDateMap.keySet()) {
			realizedQuotes._maturityDateArray[tenorIndex] = maturityDate.julian();

			realizedQuotes._maturityYieldArray[tenorIndex++] =
				tenorYieldMap.get (maturityDateMap.get (maturityDate));
		}

		return realizedQuotes;
	}

	private boolean taxExemptGovvieCurveMap (
		final String currency)
	{
		for (JulianDate asOfDate : _taxExemptDateTenorYieldRealizationMap.keySet()) {
			RealizedQuotes realizedQuotes = realizedQuotes (
				asOfDate,
				_taxExemptDateTenorYieldRealizationMap.get (asOfDate)
			);

			if (null == realizedQuotes) {
				return false;
			}

			_dateTaxExemptGovvieCurveMap.put (
				asOfDate,
				ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
					"TAX_EXEMPT_" + asOfDate,
					asOfDate,
					"UST",
					currency,
					realizedQuotes._maturityDateArray,
					realizedQuotes._maturityYieldArray
				)
			);
		}

		return true;
	}

	private boolean taxableGovvieCurveMap (
		final String currency)
	{
		for (JulianDate asOfDate : _taxableDateTenorYieldRealizationMap.keySet()) {
			RealizedQuotes realizedQuotes = realizedQuotes (
				asOfDate,
				_taxableDateTenorYieldRealizationMap.get (asOfDate)
			);

			if (null == realizedQuotes) {
				return false;
			}

			_dateTaxableGovvieCurveMap.put (
				asOfDate,
				ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
					"TAXABLE_" + asOfDate,
					asOfDate,
					"UST",
					currency,
					realizedQuotes._maturityDateArray,
					realizedQuotes._maturityYieldArray
				)
			);
		}

		return true;
	}

	private boolean escrowGovvieCurveMap (
		final String currency)
	{
		for (JulianDate asOfDate : _escrowDateTenorYieldRealizationMap.keySet()) {
			RealizedQuotes realizedQuotes = realizedQuotes (
				asOfDate,
				_escrowDateTenorYieldRealizationMap.get (asOfDate)
			);

			if (null == realizedQuotes) {
				return false;
			}

			_dateEscrowGovvieCurveMap.put (
				asOfDate,
				ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
					"ESCROW_" + asOfDate,
					asOfDate,
					"UST",
					currency,
					realizedQuotes._maturityDateArray,
					realizedQuotes._maturityYieldArray
				)
			);
		}

		return true;
	}

	/**
	 * Empty <i>DeGuillaumeRebonatoPogudinPath</i> Constructor
	 */

	public DeGuillaumeRebonatoPogudinPath()
	{
		_taxExemptDateTenorYieldRealizationMap = new TreeMap<JulianDate, Map<String, Double>>();

		_taxableDateTenorYieldRealizationMap = new TreeMap<JulianDate, Map<String, Double>>();

		_escrowDateTenorYieldRealizationMap = new TreeMap<JulianDate, Map<String, Double>>();

		_dateTaxExemptGovvieCurveMap = new TreeMap<JulianDate, GovvieCurve>();

		_dateTaxableGovvieCurveMap = new TreeMap<JulianDate, GovvieCurve>();

		_dateEscrowGovvieCurveMap = new TreeMap<JulianDate, GovvieCurve>();
	}

	/**
	 * Retrieve the Double Map of Tax-Exempt Date/Tenor Yield Realization
	 * 
	 * @return Double Map of Tax-Exempt Date/Tenor Yield Realization
	 */

	public TreeMap<JulianDate, Map<String, Double>> taxExemptDateTenorYieldRealizationMap()
	{
		return _taxExemptDateTenorYieldRealizationMap;
	}

	/**
	 * Retrieve the Double Map of Taxable Date/Tenor Yield Realization
	 * 
	 * @return Double Map of Taxable Date/Tenor Yield Realization
	 */

	public TreeMap<JulianDate, Map<String, Double>> taxableDateTenorYieldRealizationMap()
	{
		return _taxableDateTenorYieldRealizationMap;
	}

	/**
	 * Retrieve the Double Map of Escrow Date/Tenor Yield Realization
	 * 
	 * @return Double Map of Escrow Date/Tenor Yield Realization
	 */

	public TreeMap<JulianDate, Map<String, Double>> escrowDateTenorYieldRealizationMap()
	{
		return _escrowDateTenorYieldRealizationMap;
	}

	/**
	 * Construct a Map of Simulation Dates and their Tax-exempt Yield Curves
	 * 
	 * @return Map of Simulation Dates and their Tax-exempt Yield Curves
	 */

	public TreeMap<JulianDate, GovvieCurve> dateTaxExemptGovvieCurveMap()
	{
		return _dateTaxExemptGovvieCurveMap;
	}

	/**
	 * Construct a Map of Simulation Dates and their Taxable Yield Curves
	 * 
	 * @return Map of Simulation Dates and their Taxable Yield Curves
	 */

	public TreeMap<JulianDate, GovvieCurve> dateTaxableGovvieCurveMap()
	{
		return _dateTaxableGovvieCurveMap;
	}

	/**
	 * Construct a Map of Simulation Dates and their Escrow Yield Curves
	 * 
	 * @return Map of Simulation Dates and their Escrow Yield Curves
	 */

	public TreeMap<JulianDate, GovvieCurve> dateEscrowGovvieCurveMap()
	{
		return _dateEscrowGovvieCurveMap;
	}

	/**
	 * Retrieve the Tenor Key Set
	 * 
	 * @return Tenor Key Set
	 */

	public Set<String> tenorKeySet()
	{
		return _taxExemptDateTenorYieldRealizationMap.isEmpty() ?
			null : _taxExemptDateTenorYieldRealizationMap.firstEntry().getValue().keySet();
	}

	/**
	 * Add the Tax-exempt Tenor-to-Yield Realization Map at the given Date Node
	 * 
	 * @param date Date Node
	 * @param tenorYieldRealizationMap Tenor-to-Yield Realization Map
	 * 
	 * @return TRUE - Tax-exempt Tenor-to-Yield Realization Map successfully added at the given Date Node
	 */

	public boolean addTaxExemptDateTenorYieldRealization (
		final JulianDate date,
		final Map<String, Double> tenorYieldRealizationMap)
	{
		if (null == date || null == tenorYieldRealizationMap || 0 == tenorYieldRealizationMap.size()) {
			return false;
		}

		_taxExemptDateTenorYieldRealizationMap.put (date, tenorYieldRealizationMap);

		return true;
	}

	/**
	 * Add the Tax-exempt Yield Realization corresponding to the Date and Tenor
	 * 
	 * @param date Date Node
	 * @param tenor Tenor Node
	 * @param dateTenorYield Tax-exempt Yield Realization corresponding to the Date and Tenor
	 * 
	 * @return TRUE - Tax-exempt Yield Realization corresponding to the Date and Tenor successfully added
	 */

	public boolean addTaxExemptDateTenorYieldRealization (
		final JulianDate date,
		final String tenor,
		final double dateTenorYield)
	{
		if (null == date || null == tenor || tenor.isEmpty() || !NumberUtil.IsValid (dateTenorYield)) {
			return false;
		}

		if (!_taxExemptDateTenorYieldRealizationMap.containsKey (date)) {
			_taxExemptDateTenorYieldRealizationMap.put (date, new CaseInsensitiveHashMap<Double>());
		}

		_taxExemptDateTenorYieldRealizationMap.get (date).put (tenor, dateTenorYield);

		return true;
	}

	/**
	 * Add the Taxable Tenor-to-Yield Realization Map at the given Date Node
	 * 
	 * @param date Date Node
	 * @param tenorYieldRealizationMap Tenor-to-Yield Realization Map
	 * 
	 * @return TRUE - Taxable Tenor-to-Yield Realization Map successfully added at the given Date Node
	 */

	public boolean addTaxableDateTenorYieldRealization (
		final JulianDate date,
		final Map<String, Double> tenorYieldRealizationMap)
	{
		if (null == date || null == tenorYieldRealizationMap || 0 == tenorYieldRealizationMap.size()) {
			return false;
		}

		_taxableDateTenorYieldRealizationMap.put (date, tenorYieldRealizationMap);

		return true;
	}

	/**
	 * Add the Taxable Yield Realization corresponding to the Date and Tenor
	 * 
	 * @param date Date Node
	 * @param tenor Tenor Node
	 * @param dateTenorYield Taxable Yield Realization corresponding to the Date and Tenor
	 * 
	 * @return TRUE - Taxable Yield Realization corresponding to the Date and Tenor successfully added
	 */

	public boolean addTaxableDateTenorYieldRealization (
		final JulianDate date,
		final String tenor,
		final double dateTenorYield)
	{
		if (null == date || null == tenor || tenor.isEmpty() || !NumberUtil.IsValid (dateTenorYield)) {
			return false;
		}

		if (!_taxableDateTenorYieldRealizationMap.containsKey (date)) {
			_taxableDateTenorYieldRealizationMap.put (date, new CaseInsensitiveHashMap<Double>());
		}

		_taxableDateTenorYieldRealizationMap.get (date).put (tenor, dateTenorYield);

		return true;
	}

	/**
	 * Add the Escrow Tenor-to-Yield Realization Map at the given Date Node
	 * 
	 * @param date Date Node
	 * @param tenorYieldRealizationMap Tenor-to-Yield Realization Map
	 * 
	 * @return TRUE - Escrow Tenor-to-Yield Realization Map successfully added at the given Date Node
	 */

	public boolean addEscrowDateTenorYieldRealization (
		final JulianDate date,
		final Map<String, Double> tenorYieldRealizationMap)
	{
		if (null == date || null == tenorYieldRealizationMap || 0 == tenorYieldRealizationMap.size()) {
			return false;
		}

		_escrowDateTenorYieldRealizationMap.put (date, tenorYieldRealizationMap);

		return true;
	}

	/**
	 * Add the Escrow Yield Realization corresponding to the Date and Tenor
	 * 
	 * @param date Date Node
	 * @param tenor Tenor Node
	 * @param dateTenorYield Escrow Yield Realization corresponding to the Date and Tenor
	 * 
	 * @return TRUE - Escrow Yield Realization corresponding to the Date and Tenor successfully added
	 */

	public boolean addEscrowDateTenorYieldRealization (
		final JulianDate date,
		final String tenor,
		final double dateTenorYield)
	{
		if (null == date || null == tenor || tenor.isEmpty() || !NumberUtil.IsValid (dateTenorYield)) {
			return false;
		}

		if (!_escrowDateTenorYieldRealizationMap.containsKey (date)) {
			_escrowDateTenorYieldRealizationMap.put (date, new CaseInsensitiveHashMap<Double>());
		}

		_escrowDateTenorYieldRealizationMap.get (date).put (tenor, dateTenorYield);

		return true;
	}

	/**
	 * Setup the Govvie Curve Map
	 * 
	 * @param currency
	 * 
	 * @return TRUE - Date to Tax-exempt, Taxable, and Escrow Govvie Curves successfully set
	 */

	public boolean setUpGovvieCurveMap (
		final String currency)
	{
		return taxExemptGovvieCurveMap (currency) &&
			taxableGovvieCurveMap (currency) &&
			escrowGovvieCurveMap (currency);
	}

	/**
	 * Retrieve the Map of the Simulation Date to Tax-exempt Negative Arbitrage Indicator at the Target Date
	 * 
	 * @param targetDate Target Date
	 * 
	 * @return Map of the Simulation Date to Tax-exempt Negative Arbitrage Indicator at the Target Date
	 */

	public TreeMap<JulianDate, Boolean> dateTaxExemptNegativeArbitrageMap (
		final JulianDate targetDate)
	{
		if (null == targetDate) {
			return null;
		}

		TreeMap<JulianDate, Boolean> dateTaxExemptNegativeArbitrageMap = new TreeMap<JulianDate, Boolean>();

		try {
			for (JulianDate date : _escrowDateTenorYieldRealizationMap.keySet()) {
				dateTaxExemptNegativeArbitrageMap.put (
					date,
					_dateEscrowGovvieCurveMap.get (date).yld (targetDate) <
						_dateTaxExemptGovvieCurveMap.get (date).yld (targetDate)
				);
			}
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		return dateTaxExemptNegativeArbitrageMap;
	}

	/**
	 * Retrieve the Map of the Simulation Date to Taxable Negative Arbitrage Indicator at the Target Date
	 * 
	 * @param targetDate Target Date
	 * 
	 * @return Map of the Simulation Date to Taxable Negative Arbitrage Indicator at the Target Date
	 */

	public TreeMap<JulianDate, Boolean> dateTaxableNegativeArbitrageMap (
		final JulianDate targetDate)
	{
		if (null == targetDate) {
			return null;
		}

		TreeMap<JulianDate, Boolean> dateTaxableNegativeArbitrageMap = new TreeMap<JulianDate, Boolean>();

		try {
			for (JulianDate date : _escrowDateTenorYieldRealizationMap.keySet()) {
				dateTaxableNegativeArbitrageMap.put (
					date,
					_dateEscrowGovvieCurveMap.get (date).yld (targetDate) <
						_dateTaxableGovvieCurveMap.get (date).yld (targetDate)
				);
			}
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		return dateTaxableNegativeArbitrageMap;
	}
}
