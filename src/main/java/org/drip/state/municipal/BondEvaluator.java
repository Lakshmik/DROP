
package org.drip.state.municipal;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.JulianDate;
import org.drip.product.credit.BondComponent;
import org.drip.product.params.EmbeddedOptionSchedule;

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
 * <i>BondEvaluator</i> implements the DRIP Proxy Evaluator to be used in the Kalotay, Williams, and Fabozzi
 * 	(1993) Tree-based Model for valuing bonds with Embedded Options. The References are:
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

public class BondEvaluator
{
	private BondComponent _underlying = null;

	/**
	 * <i>BondEvaluator</i> Constructor
	 * 
	 * @param underlying Underlying Calibration Bond
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public BondEvaluator (
		final BondComponent underlying)
		throws Exception
	{
		if (null == (_underlying = underlying)) {
			throw new Exception ("BondEvaluator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Underlying Calibration Bond
	 * 
	 * @return Underlying Calibration Bond
	 */

	public BondComponent underlying()
	{
		return _underlying;
	}

	/**
	 * Construct the Bond Coupon Cash Flow PV Tree using the Calibrated <i>KalotayWilliamsFabozzi</i>
	 *  Instance
	 * 
	 * @param spotDate Spot Date
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return Bond Value Tree
	 */

	public TreeMap<Double, List<Double>> cashFlowPVValueTree (
		final JulianDate spotDate,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		if (null == spotDate || null == kalotayWilliamsFabozzi) {
			return null;
		}

		int spotDateJulian = spotDate.julian();

		TreeMap<Double, List<Double>> cashFlowPVValueTree = new TreeMap<Double, List<Double>>();

		for (CompositePeriod compositePeriod : _underlying.couponPeriods()) {
			int payDateJulian = compositePeriod.payDate();

			if (spotDateJulian >= payDateJulian) {
				continue;
			}

			double cashflow = Double.NaN;
			double periodStartTime = ((double) (compositePeriod.startDate() - spotDateJulian)) / 365.25;

			try {
				cashflow = _underlying.couponSetting().couponRate() * compositePeriod.couponDCF();
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			List<KalotayWilliamsFabozziPeriodState> bespokePeriodStateList =
				kalotayWilliamsFabozzi.bespokePeriodStateList (
					((double) (payDateJulian - spotDateJulian)) / 365.25
				);

			for (KalotayWilliamsFabozziPeriodState bespokePeriodState : bespokePeriodStateList) {
				if (!cashFlowPVValueTree.containsKey (periodStartTime)) {
					cashFlowPVValueTree.put (periodStartTime, new ArrayList<Double>());
				}

				cashFlowPVValueTree.get (
					periodStartTime
				).add (
					cashflow * bespokePeriodState.cumulativeEndDiscountFactor()
				);
			}
		}

		return cashFlowPVValueTree;
	}

	/**
	 * Construct the Date to Cash Flow PV Map
	 * 
	 * @param spotDate Spot Date
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return Date to Cash Flow PV Map
	 */

	public TreeMap<JulianDate, Double> dateCashflowPVMap (
		final JulianDate spotDate,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		TreeMap<Double, List<Double>> cashFlowPVValueTree = cashFlowPVValueTree (
			spotDate,
			kalotayWilliamsFabozzi
		);

		if (null == cashFlowPVValueTree) {
			return null;
		}

		TreeMap<JulianDate, Double> dateCashflowPVMap = new TreeMap<JulianDate, Double>();

		for (Double cashFlowTime : cashFlowPVValueTree.keySet()) {
			double cumulativeCashFlowPV = 0.;

			JulianDate payDate = spotDate.addDays ((int) (365.25 * cashFlowTime));

			List<Double> periodCashflowPVList = cashFlowPVValueTree.get (cashFlowTime);

			for (Double cashflowPV : periodCashflowPVList) {
				cumulativeCashFlowPV += cashflowPV;
			}

			dateCashflowPVMap.put (payDate, cumulativeCashFlowPV / periodCashflowPVList.size());
		}

		return dateCashflowPVMap;
	}

	/**
	 * Evaluate the EOS Value Tree
	 * 
	 * @param spotDate Spot Date
	 * @param maturityDate Maturity Date
	 * @param embeddedOptionSchedule Bond Embedded Option Schedule
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * @param dateCashflowPVMap Date to Cash Flow PV Map
	 * 
	 * @return EOS Value Tree
	 */

	public TreeMap<Double, List<Double>> eosValueTree (
		final JulianDate spotDate,
		final JulianDate maturityDate,
		final EmbeddedOptionSchedule embeddedOptionSchedule,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi,
		final TreeMap<JulianDate, Double> dateCashflowPVMap)
	{
		if (null == spotDate ||
			null == embeddedOptionSchedule ||
			null == kalotayWilliamsFabozzi ||
			null == dateCashflowPVMap)
		{
			return null;
		}

		int spotDateJulian = spotDate.julian();

		TreeMap<Double, Double> exerciseFactorMap = new TreeMap<Double, Double>();

		int[] exerciseDateArray = embeddedOptionSchedule.exerciseDates (spotDateJulian);

		TreeMap<Double, List<Double>> eosValueTree = new TreeMap<Double, List<Double>>();

		double[] exerciseFactorArray = embeddedOptionSchedule.exerciseFactors (spotDateJulian);

		for (int exerciseDateIndex = 0; exerciseDateIndex < exerciseDateArray.length; ++exerciseDateIndex) {
			double exerciseTime =
				((double) (exerciseDateArray[exerciseDateIndex] - spotDateJulian)) / 365.25;

			eosValueTree.put (exerciseTime, new ArrayList<Double>());

			exerciseFactorMap.put (exerciseTime, exerciseFactorArray[exerciseDateIndex]);
		}

		return eosValueTree;
	}

	/**
	 * Construct the Bond Value Tree using the Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @param spotDate Spot Date
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return Bond Value Tree
	 */

	public TreeMap<Double, List<Double>> valueTree (
		final JulianDate spotDate,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		TreeMap<JulianDate, Double> dateCashflowPVMap = dateCashflowPVMap (spotDate, kalotayWilliamsFabozzi);

		if (null == dateCashflowPVMap) {
			return null;
		}

		EmbeddedOptionSchedule putSchedule = _underlying.putSchedule();

		EmbeddedOptionSchedule callSchedule = _underlying.callSchedule();

		if (null == callSchedule && null == putSchedule) {
			double cashFlowPV = 0.;

			for (double periodCashFlowPV : dateCashflowPVMap.values()) {
				cashFlowPV += periodCashFlowPV;
			}

			List<Double> cashFlowList = new ArrayList<Double>();

			cashFlowList.add (cashFlowPV + dateCashflowPVMap.lastEntry().getValue());

			TreeMap<Double, List<Double>> valueTree = new TreeMap<Double, List<Double>>();

			valueTree.put (0., cashFlowList);

			return valueTree;
		}

		double cashFlowPV = 0.;

		for (double periodCashFlowPV : dateCashflowPVMap.values()) {
			cashFlowPV += periodCashFlowPV;
		}

		List<Double> cashFlowList = new ArrayList<Double>();

		cashFlowList.add (cashFlowPV + dateCashflowPVMap.lastEntry().getValue());

		TreeMap<Double, List<Double>> valueTree = new TreeMap<Double, List<Double>>();

		valueTree.put (0., cashFlowList);

		return valueTree;
	}
}
