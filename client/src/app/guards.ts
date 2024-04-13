import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { GuardService } from "./guard.service";
import { lastValueFrom } from "rxjs";
import { AppInitialiserService } from "./app-initialiser.service";

export const isLoggedIn: CanActivateFn=
    (route, state)=>{
        const guardService = inject(GuardService)
        const router = inject(Router)
        if(guardService.loggedIn){
            // console.log("guardservice is logged in")
            return true
        }
        // console.log("guard service is logged out")
        return router.parseUrl("/")
    }
export const isLoggedOut: CanActivateFn=
    (route, state)=>{
        const guardService = inject(GuardService)
        const router = inject(Router)
        if(!guardService.loggedIn){
            return true
        }
        return router.parseUrl("/")
    }