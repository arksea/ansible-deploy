import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { PageEvent } from '@angular/material/paginator'
import { Page } from '../app.entity'

export class PagedSearchableList<T>  {
    
    page: Page<T> = new Page()

    constructor(public pageSize: number) {
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    })

    search(event: FormDataEvent) {
        event.preventDefault()
        let pre = this.searchForm.get('searchPrefix').value
        this.query(0, pre)
    }

    public onPageEvent(event: PageEvent): PageEvent {
        let page = event.pageIndex + 1
        this.pageSize = event.pageSize
        let search = this.searchForm.get('searchPrefix').value
        this.query(page, search)
        return event
    }

    query(page:number, search: string) {}
}