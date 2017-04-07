//
//  TodayViewController.swift
//  MedicationWidget
//
//  Created by Chiew Carol on 07/04/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

import UIKit
import NotificationCenter

class TodayViewController: UIViewController, NCWidgetProviding {
        
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view from its nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
 
    @IBAction func openMedicationScreen(_ sender: Any) {
        let myAppUrl = URL(string: "recapp://P0001")!
        extensionContext?.open(myAppUrl, completionHandler: { (success) in
            if (!success) {
                print("error: failed to open app from Today Extension")
            }
        })
    }
    
}
