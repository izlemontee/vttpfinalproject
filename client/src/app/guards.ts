import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { GuardService } from "./guard.service";

export const isLoggedIn: CanActivateFn=
    (route, state)=>{
        const guardService = inject(GuardService)
        const router = inject(Router)
        guardService.getLoginObservable().subscribe({
            next:(response)=>{
                console.log("guard response:", response)
            }
        })
        if(guardService.isLoggedIn()){
            console.log("guardservice is logged in")
            return true
        }
        console.log("guard service is logged out")
        return router.parseUrl("/")
    }