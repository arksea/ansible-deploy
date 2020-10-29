import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AuthGuard } from '../account/auth-guard'
import { GroupsComponent } from './groups.component'
import { GroupListComponent } from './group-list.component'
import { GroupComponent } from './group.component'
import { GroupMembersComponent } from './group-members.component'
import { GroupAppsComponent } from './group-apps.component'
import { GroupHostsComponent } from './group-hosts.component'


const routes: Routes = [
    {
        path: 'groups', component: GroupsComponent, canActivate: [AuthGuard],
        children: [
            { path: '', component: GroupListComponent, canActivate: [AuthGuard] },
            {
                path: ':id', component: GroupComponent, canActivate: [AuthGuard],
                children: [
                    { path: 'members', component: GroupMembersComponent, canActivate: [AuthGuard] },
                    { path: 'apps', component: GroupAppsComponent, canActivate: [AuthGuard] },
                    { path: 'hosts', component: GroupHostsComponent, canActivate: [AuthGuard] }
                ]
            }
        ]
    }
]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class GroupsRoutingModule { }
