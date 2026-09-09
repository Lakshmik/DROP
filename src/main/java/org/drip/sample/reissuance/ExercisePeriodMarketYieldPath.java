
package org.drip.sample.reissuance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.product.muni.DeGuillaumeRebonatoPogudin;
import org.drip.product.muni.DeGuillaumeRebonatoPogudinMarketYield;
import org.drip.product.muni.DeGuillaumeRebonatoPogudinPath;
import org.drip.product.muni.MarketYieldTermStructure;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
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
 * <i>ExercisePeriodMarketYieldPath</i> illustrates the Simulation of a single-correlated Path of Tax-exempt,
 *  Taxable, and Escrow Yields across the Dates in an Exercise Period. The Simulated Yields are Anchored at
 *  the Dated Nodes. The References are:
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
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/reissuance/README.md">Optimal Exercise/Bond Re-issuance Calculations</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ExercisePeriodMarketYieldPath
{

	private static final MarketYieldTermStructure TaxExemptMarketYieldTermStructure()
		throws Exception
	{
		Map<String, Double> spotTenorValueMap = new CaseInsensitiveHashMap<Double>();

		spotTenorValueMap.put ("3M", 0.00175);

		spotTenorValueMap.put ("6M", 0.00258);

		spotTenorValueMap.put ("1Y", 0.00320);

		spotTenorValueMap.put ("2Y", 0.00450);

		spotTenorValueMap.put ("3Y", 0.00660);

		spotTenorValueMap.put ("4Y", 0.00870);

		spotTenorValueMap.put ("5Y", 0.01020);

		spotTenorValueMap.put ("7Y", 0.01520);

		spotTenorValueMap.put ("10Y", 0.02180);

		spotTenorValueMap.put ("12Y", 0.02640);

		spotTenorValueMap.put ("15Y", 0.03010);

		spotTenorValueMap.put ("20Y", 0.03490);

		spotTenorValueMap.put ("30Y", 0.04030);

		Map<String, Double> infiniteHorizonTenorValueMap = new CaseInsensitiveHashMap<Double>();

		infiniteHorizonTenorValueMap.put ("3M", 0.02791);

		infiniteHorizonTenorValueMap.put ("6M", 0.02924);

		infiniteHorizonTenorValueMap.put ("1Y", 0.03000);

		infiniteHorizonTenorValueMap.put ("2Y", 0.03450);

		infiniteHorizonTenorValueMap.put ("3Y", 0.03833);

		infiniteHorizonTenorValueMap.put ("4Y", 0.04158);

		infiniteHorizonTenorValueMap.put ("5Y", 0.04434);

		infiniteHorizonTenorValueMap.put ("7Y", 0.04669);

		infiniteHorizonTenorValueMap.put ("10Y", 0.04869);

		infiniteHorizonTenorValueMap.put ("12Y", 0.05038);

		infiniteHorizonTenorValueMap.put ("15Y", 0.05183);

		infiniteHorizonTenorValueMap.put ("20Y", 0.05305);

		infiniteHorizonTenorValueMap.put ("30Y", 0.05409);

		return new MarketYieldTermStructure (spotTenorValueMap, infiniteHorizonTenorValueMap);
	}

	private static final MarketYieldTermStructure TaxableMarketYieldTermStructure()
		throws Exception
	{
		Map<String, Double> spotTenorValueMap = new CaseInsensitiveHashMap<Double>();

		spotTenorValueMap.put ("3M", 0.00321);

		spotTenorValueMap.put ("6M", 0.00362);

		spotTenorValueMap.put ("1Y", 0.00432);

		spotTenorValueMap.put ("2Y", 0.00668);

		spotTenorValueMap.put ("3Y", 0.00785);

		spotTenorValueMap.put ("4Y", 0.00855);

		spotTenorValueMap.put ("5Y", 0.01414);

		spotTenorValueMap.put ("7Y", 0.01983);

		spotTenorValueMap.put ("10Y", 0.02731);

		spotTenorValueMap.put ("12Y", 0.03171);

		spotTenorValueMap.put ("15Y", 0.03461);

		spotTenorValueMap.put ("20Y", 0.03762);

		spotTenorValueMap.put ("30Y", 0.04092);

		Map<String, Double> infiniteHorizonTenorValueMap = new CaseInsensitiveHashMap<Double>();

		infiniteHorizonTenorValueMap.put ("3M", 0.03408);

		infiniteHorizonTenorValueMap.put ("6M", 0.03863);

		infiniteHorizonTenorValueMap.put ("1Y", 0.04296);

		infiniteHorizonTenorValueMap.put ("2Y", 0.04743);

		infiniteHorizonTenorValueMap.put ("3Y", 0.05019);

		infiniteHorizonTenorValueMap.put ("4Y", 0.05746);

		infiniteHorizonTenorValueMap.put ("5Y", 0.05665);

		infiniteHorizonTenorValueMap.put ("7Y", 0.05935);

		infiniteHorizonTenorValueMap.put ("10Y", 0.06168);

		infiniteHorizonTenorValueMap.put ("12Y", 0.06451);

		infiniteHorizonTenorValueMap.put ("15Y", 0.06576);

		infiniteHorizonTenorValueMap.put ("20Y", 0.06648);

		infiniteHorizonTenorValueMap.put ("30Y", 0.06655);

		return new MarketYieldTermStructure (spotTenorValueMap, infiniteHorizonTenorValueMap);
	}

	private static final MarketYieldTermStructure EscrowMarketYieldTermStructure()
		throws Exception
	{
		Map<String, Double> spotTenorValueMap = new CaseInsensitiveHashMap<Double>();

		spotTenorValueMap.put ("3M", 0.00071);

		spotTenorValueMap.put ("6M", 0.00112);

		spotTenorValueMap.put ("1Y", 0.00130);

		spotTenorValueMap.put ("2Y", 0.00240);

		spotTenorValueMap.put ("3Y", 0.00350);

		spotTenorValueMap.put ("4Y", 0.00520);

		spotTenorValueMap.put ("5Y", 0.00750);

		spotTenorValueMap.put ("7Y", 0.01210);

		spotTenorValueMap.put ("10Y", 0.01860);

		spotTenorValueMap.put ("12Y", 0.02130);

		spotTenorValueMap.put ("15Y", 0.02390);

		spotTenorValueMap.put ("20Y", 0.02700);

		spotTenorValueMap.put ("30Y", 0.03090);

		Map<String, Double> infiniteHorizonTenorValueMap = new CaseInsensitiveHashMap<Double>();

		infiniteHorizonTenorValueMap.put ("3M", 0.03158);

		infiniteHorizonTenorValueMap.put ("6M", 0.03613);

		infiniteHorizonTenorValueMap.put ("1Y", 0.03994);

		infiniteHorizonTenorValueMap.put ("2Y", 0.04315);

		infiniteHorizonTenorValueMap.put ("3Y", 0.04584);

		infiniteHorizonTenorValueMap.put ("4Y", 0.04811);

		infiniteHorizonTenorValueMap.put ("5Y", 0.05001);

		infiniteHorizonTenorValueMap.put ("7Y", 0.05162);

		infiniteHorizonTenorValueMap.put ("10Y", 0.05297);

		infiniteHorizonTenorValueMap.put ("12Y", 0.05410);

		infiniteHorizonTenorValueMap.put ("15Y", 0.05505);

		infiniteHorizonTenorValueMap.put ("20Y", 0.05586);

		infiniteHorizonTenorValueMap.put ("30Y", 0.05653);

		return new MarketYieldTermStructure (spotTenorValueMap, infiniteHorizonTenorValueMap);
	}

	private static final List<String> TenorList()
	{
		List<String> tenorList = new ArrayList<String>();

		tenorList.add ("3M");

		tenorList.add ("6M");

		tenorList.add ("1Y");

		tenorList.add ("2Y");

		tenorList.add ("3Y");

		tenorList.add ("4Y");

		tenorList.add ("5Y");

		tenorList.add ("7Y");

		tenorList.add ("10Y");

		tenorList.add ("12Y");

		tenorList.add ("15Y");

		tenorList.add ("20Y");

		tenorList.add ("30Y");

		return tenorList;
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String currency = "USD";
		double burstiness = 0.002;
		double relaxationTime = 50;

		JulianDate spotDate = DateUtil.Today();

		JulianDate exerciseStartDate = spotDate.addYears (1);

		List<JulianDate> simulationDateList = new ArrayList<JulianDate>();

		simulationDateList.add (exerciseStartDate);

		simulationDateList.add (exerciseStartDate.addBusDays (1, "USD"));

		simulationDateList.add (exerciseStartDate.addBusDays (2, "USD"));

		simulationDateList.add (exerciseStartDate.addBusDays (3, "USD"));

		simulationDateList.add (exerciseStartDate.addBusDays (4, "USD"));

		simulationDateList.add (exerciseStartDate.addBusDays (5, "USD"));

		DeGuillaumeRebonatoPogudinMarketYield deGuillaumeRebonatoPogudinMarketYield =
			new DeGuillaumeRebonatoPogudinMarketYield (
				TaxExemptMarketYieldTermStructure(),
				TaxableMarketYieldTermStructure(),
				EscrowMarketYieldTermStructure()
			);

		DeGuillaumeRebonatoPogudin deGuillaumeRebonatoPogudin = DeGuillaumeRebonatoPogudin.Standard (
			currency,
			burstiness,
			relaxationTime,
			deGuillaumeRebonatoPogudinMarketYield
		);

		DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinPath = deGuillaumeRebonatoPogudin.evolve (
			deGuillaumeRebonatoPogudinMarketYield,
			spotDate,
			simulationDateList
		);

		TreeMap<JulianDate, Map<String, Double>> taxExemptDateTenorYieldRealizationMap =
			deGuillaumeRebonatoPogudinPath.taxExemptDateTenorYieldRealizationMap();

		Map<JulianDate, GovvieCurve> taxExemptGovvieCurveMap =
			deGuillaumeRebonatoPogudinPath.dateTaxExemptGovvieCurveMap();

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||    TAX EXEMPT REALIZED DATE-TENOR YIELD MAP");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||        - Date");

		System.out.println ("\t||        - Tenor Yield Simulated");

		System.out.println ("\t||        - Tenor Yield Re-calibrated");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		for (JulianDate date : taxExemptDateTenorYieldRealizationMap.keySet()) {
			String dump = "\t|| " + date + " =>";

			Map<String, Double> tenorYieldMap = taxExemptDateTenorYieldRealizationMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (tenorYieldMap.get (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			dump = "\t|| " + date + " =>";

			GovvieCurve govvieCurve = taxExemptGovvieCurveMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (govvieCurve.yld (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			System.out.println (
				"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
			);
		}

		System.out.println();

		TreeMap<JulianDate, Map<String, Double>> taxableDateTenorYieldRealizationMap =
			deGuillaumeRebonatoPogudinPath.taxableDateTenorYieldRealizationMap();

		Map<JulianDate, GovvieCurve> taxableGovvieCurveMap =
			deGuillaumeRebonatoPogudinPath.dateTaxableGovvieCurveMap();

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||    TAXABLE REALIZED DATE-TENOR YIELD MAP");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||        - Date");

		System.out.println ("\t||        - Tenor Yield Simulated");

		System.out.println ("\t||        - Tenor Yield Re-calibrated");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		for (JulianDate date : taxableDateTenorYieldRealizationMap.keySet()) {
			String dump = "\t|| " + date + " =>";

			Map<String, Double> tenorYieldMap = taxableDateTenorYieldRealizationMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (tenorYieldMap.get (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			dump = "\t|| " + date + " =>";

			GovvieCurve govvieCurve = taxableGovvieCurveMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (govvieCurve.yld (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			System.out.println (
				"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
			);
		}

		System.out.println();

		TreeMap<JulianDate, Map<String, Double>> escrowDateTenorYieldRealizationMap =
			deGuillaumeRebonatoPogudinPath.escrowDateTenorYieldRealizationMap();

		Map<JulianDate, GovvieCurve> escrowGovvieCurveMap =
			deGuillaumeRebonatoPogudinPath.dateEscrowGovvieCurveMap();

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||    ESCROW REALIZED DATE-TENOR YIELD MAP");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println ("\t||        - Date");

		System.out.println ("\t||        - Tenor Yield Simulated");

		System.out.println ("\t||        - Tenor Yield Re-calibrated");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
		);

		for (JulianDate date : escrowDateTenorYieldRealizationMap.keySet()) {
			String dump = "\t|| " + date + " =>";

			Map<String, Double> tenorYieldMap = escrowDateTenorYieldRealizationMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (tenorYieldMap.get (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			dump = "\t|| " + date + " =>";

			GovvieCurve govvieCurve = escrowGovvieCurveMap.get (date);

			for (String tenor : TenorList()) {
				dump += " " + tenor + ":" + FormatUtil.FormatDouble (govvieCurve.yld (tenor), 1, 4, 100.) +
					"% |";
			}

			System.out.println (dump + "|");

			System.out.println (
				"\t||---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||"
			);
		}

		System.out.println();

		EnvManager.TerminateEnv();
	}
}
