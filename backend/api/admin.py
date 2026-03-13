from django.contrib import admin
from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession

admin.site.register(AnnualGoal)
admin.site.register(Author)
admin.site.register(Genre)
admin.site.register(Book)
admin.site.register(Reading)
admin.site.register(ReadingSession)
